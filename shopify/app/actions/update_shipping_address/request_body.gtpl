{
    "query": {{ toJson `mutation OrderUpdate($input: OrderInput!) {
        orderUpdate(input: $input) {
            order {
                id
                customer {
                    id
                    displayName
                }
                confirmationNumber
                fulfillable
                fulfillmentsCount {count precision}
                fulfillments {
                    id
                    fulfillmentLineItems(first:10) {
                        nodes {
                                id
                                quantity
                                originalTotalSet{
                                    shopMoney { amount currencyCode }
                                }
                                discountedTotalSet {
                                    shopMoney { amount currencyCode }
                                }
                                lineItem {
                                    id
                                    
                                }
                        }
                    }
                    createdAt
                    updatedAt
                    deliveredAt
                    displayStatus
                    estimatedDeliveryAt
                    inTransitAt
                    name
                    originAddress {
                        address1
                        address2
                        city
                        countryCode
                        provinceCode
                        zip
                    }
                    requiresShipping
                    status
                    trackingInfo {
                        company
                        number
                        url
                    }
                }
                statusPageUrl
                createdAt
                email
                name
                note
                tags
                updatedAt
                currencyCode
                cancelReason
                currentSubtotalPriceSet {
                    shopMoney {
                        amount
                        currencyCode
                    }
                }
                currentTotalDiscountsSet {
                    shopMoney {
                        amount
                        currencyCode
                    }
                }
                currentTotalTaxSet {
                    shopMoney {
                        amount
                        currencyCode
                    }
                }
                currentTotalPriceSet {
                    shopMoney {
                        amount
                        currencyCode
                    }
                }
                subtotalPriceSet {
                    shopMoney {
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
                    countryCode
                }
                shippingLines(first: 40) {
                    nodes {
                            carrierIdentifier
                            code
                            custom
                            id
                            phone
                            source
                            title
                    }
                }
                billingAddress {
                    address1
                    address2
                    city
                    province
                    provinceCode
                    zip
                    country
                    countryCode
                }
                lineItems(first: 40) {
                    nodes {
                            id
                            name
                            product {
                                id
                                title
                                status
                                createdAt
                                productType
                                vendor
                                updatedAt
                                tags
                                variants(first: 10) {
                                    nodes {
                                            id
                                            title
                                            createdAt
                                            price
                                            sku
                                            updatedAt
                                    }
                                }
                                options {
                                    name
                                    values
                                }
                            }
                            quantity
                            sku
                            originalUnitPriceSet {
                                shopMoney {
                                    amount
                                    currencyCode
                                }
                            }
                            isGiftCard
                            totalDiscountSet {
                                shopMoney {
                                    amount
                                    currencyCode
                                }
                            }
                            variant {
                                id
                                title
                            }
                            vendor
                    }
                }
                transactions(first: 5) {
                    id
                    createdAt
                    kind
                    gateway
                    parentTransaction {
                        id
                    }
                    amountSet {
                        shopMoney {
                            amount
                            currencyCode
                        }
                    }
                }
            }
            userErrors {
                field
                message
            }
        }
    }`}},
    "variables": {
        "input": {
            "id": "{{ .inputs.orderId }}",
            {{- $shippingAddress := .inputs}}
            "shippingAddress": {
                {{ template "shippingAddressToProperties" $shippingAddress  }}
            }
        }
    }
}


{{- /* take only address related fields that are represented in input and convert them to <property>: <value> list */}}
{{- define "shippingAddressToProperties" -}}
    {{- $inputs := . }}
    {{- $firstAttr := true }}
    {{- range $index,$item := list "phone" "address1" "address2" "city" "country_code" "country" "email" "province_code" "province" "state" "zip" }}
        {{- $value := index $inputs $item }}
        {{- if and (not $firstAttr) (ne $value nil) }},{{ end }}
        {{- if ne $value nil }}
            "{{- $item}}": "{{- $value}}"
            {{- $firstAttr = false }}
        {{- end}}
    {{- end}}
{{- end -}}