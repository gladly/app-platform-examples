{{- /* The email validation is deliberately loose: it only has to be good enough
     to search with, which is what Shopify recommends for email order lookup.
     Resist tightening it — a stricter pattern rejects real addresses.

     Injection is not a concern here. The email travels as a GraphQL variable,
     so the document below is a fixed string that no input can restructure.
*/ -}}

{{- if not .inputs.email }}
    {{- stop "Email address is required to look up orders" }}
{{- end }}

{{- $email := .inputs.email | trim | lower -}}

{{- if eq $email "" }}
    {{- stop "Email address is required to look up orders" }}
{{- end }}

{{- if or (not (regexMatch `^[^\s@]+@[^\s@]+\.[^\s@]+$` $email)) (regexMatch `@\.|\.$` $email) }}
    {{- stop "Invalid email address format provided" }}
{{- end }}

{{- $ordersLimit := 10 -}}
{{- with index .integration.configuration "ordersLimit" -}}
{{- $parsed := atoi (printf "%v" .) -}}
{{- if le $parsed 0 }}{{ fail (printf "Shopify 'ordersLimit' must be a positive whole number, got %v. Order lookup by email cannot proceed." .) }}{{ end -}}
{{- $ordersLimit = $parsed -}}
{{- end -}}

{{-
$query := `
query OrdersByEmail($search: String!, $first: Int!) {
    orders(first: $first, sortKey: CREATED_AT, reverse: true, query: $search) {
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
}
`
-}}

{
    "query": {{toJson $query}},
    "variables": {{toJson (dict "search" (printf `email:"%s"` $email) "first" $ordersLimit)}}
}
