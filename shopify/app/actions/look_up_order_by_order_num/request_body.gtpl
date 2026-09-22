{{- /* SECURITY: Validate orderNum input to prevent GraphQL injection attacks

     The orderNum parameter is directly interpolated into a GraphQL query string (line 17).
     Without strict validation, an attacker could inject malicious GraphQL syntax.

     Example attack: 1" OR "1"="1

     Additionally, the 'name' field in Shopify's orders API is tokenized, meaning
     searches are performed on individual tokens (words/parts). Using double quotes
     ensures exact phrase matching, preventing data breaches where searching "1"
     could return orders "#1", "#10", "#100", "#1001", etc.

     These validations are SECURITY CONTROLS and should only be modified after
     security review. Changes to this validation logic require security approval.

     Defense layers:
     1. Check for required field
     2. Validate against character whitelist (primary security control)
     3. Double quotes in query ensure exact matching (prevents data breach)
*/ -}}

{{- /* Layer 1: Check orderNum is provided */ -}}
{{ if or (eq .inputs.orderNum nil) (eq .inputs.orderNum "") }}
	{{ stop "Input OrderNum is required."}}
{{ end }}

{{- /* Layer 2: Character whitelist validation - PRIMARY SECURITY CONTROL
     Only allow characters that appear in legitimate order numbers per Shopify docs:
     - Alphanumeric (a-zA-Z0-9): for order numbers like "1007", "EN1001"
     - Hash symbol (#): for order numbers like "#1001"
     - Dash (-): for order numbers like "1001-A"
     - Underscore (_): for variant order number formats

     This blocks GraphQL injection characters: quotes, braces, parens, backslashes
*/ -}}
{{ if regexMatch `[^a-zA-Z0-9#\-_]` .inputs.orderNum }}
	{{ stop "Order number contains invalid characters. Only alphanumeric, #, -, and _ are allowed." }}
{{ else }}
    {{- $query := printf `
{
    orders(first: 1, query: "name:\"%s\"") {
        nodes {
                id
                customer {
                    id
                    displayName
                }
                confirmationNumber
                statusPageUrl
                createdAt
                email
                name
                note
                tags
                updatedAt
                cancelReason
                currentSubtotalPriceSet {
                    shopMoney {
                        amount
                        currencyCode
                    }
                    presentmentMoney {
                        amount
                        currencyCode
                    }
                }
                currentTotalDiscountsSet {
                    shopMoney {
                        amount
                        currencyCode
                    }
                    presentmentMoney {
                        amount
                        currencyCode
                    }
                }
                currentTotalTaxSet {
                    shopMoney {
                        amount
                        currencyCode
                    }
                    presentmentMoney {
                        amount
                        currencyCode
                    }
                }
                currentTotalPriceSet {
                    shopMoney {
                        amount
                        currencyCode
                    }
                    presentmentMoney {
                        amount
                        currencyCode
                    }
                }
                displayFinancialStatus
                displayFulfillmentStatus
                shippingAddress {
                    address1
                    address2
                    city
                    province
                    provinceCode
                    zip
                    country
                    countryCodeV2
                }
                shippingLines(first: 40) {
                    nodes {
                            title
                    }
                }
                lineItems(first: 40) {
                    nodes {
                            id
                            name
                            sku
                            vendor
                            isGiftCard
                            quantity
                            variant {
                                id
                                title
                            }
                            product {
                                id
                                title
                            }
                            originalUnitPriceSet {
                                shopMoney {
                                    amount
                                    currencyCode
                                }
                            }
                            totalDiscountSet {
                                shopMoney {
                                    amount
                                }
                            }
                    }
                }
        }
    }
}` .inputs.orderNum -}}
{{- $query = $query | replace "\n" "" | replace "    " " " | trim -}}

{
    "query": {{ toJson $query}}
}
{{ end }}
