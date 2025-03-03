{{- /*Customer orders are sorted by the most recently updated. By default first 40 orders are fetched.
    Only orders belonging to a customer matched in customer data pull are currently retrieved, i.e. if
    customer created an order without an account it will not be returned. */ -}}
{{$externalCustomer := index .externalData.shopify_customer 0}}


{{ $ol := .integration.configuration.ordersLimit}}
{{- $ordersLimit := $ol | default 40 -}}

 {{- $customerId := splitList "/" $externalCustomer.id | last -}}

{{$query := printf `
query {
    orders(first: %v, reverse: true, query: "customer_id:'%s'", sortKey: UPDATED_AT) {
        edges {
            node {
                id
                customer {
                    id
                }
                fulfillable
                fulfillmentsCount {count precision}
                fulfillments {
                    id
                    fulfillmentLineItems(first:10) {
                        edges {
                            node {
                                id
                                quantity
                                originalTotalSet{
                                    shopMoney {amount}
                                }
                                discountedTotalSet {
                                    shopMoney {amount}
                                }
                                lineItem {
                                    id
                                    
                                }
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
                    presentmentMoney {
                        amount
                        currencyCode
                    }
                    shopMoney {
                        amount
                        currencyCode
                    }
                }
                currentTotalDiscountsSet {
                    presentmentMoney {
                        amount
                        currencyCode
                    }
                    shopMoney {
                        amount
                        currencyCode
                    }
                }
                currentTotalTaxSet {
                    presentmentMoney {
                        amount
                        currencyCode
                    }
                    shopMoney {
                        amount
                        currencyCode
                    }
                }
                currentTotalPriceSet {
                    presentmentMoney {
                        amount
                        currencyCode
                    }
                    shopMoney {
                        amount
                        currencyCode
                    }
                }
                subtotalPriceSet {
                    presentmentMoney {
                        amount
                        currencyCode
                    }
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
                shippingLines(first: 10) {
                    edges {
                        node {
                            carrierIdentifier
                            code
                            custom
                            id
                            phone
                            source
                            title
                        }
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
                    edges {
                        node {
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
                                    edges {
                                        node {
                                            id
                                            title
                                            createdAt
                                            price
                                            sku
                                            updatedAt
                                        }
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
                                presentmentMoney {
                                    amount
                                    currencyCode
                                }
                                shopMoney {
                                    amount
                                    currencyCode
                                }
                            }
                            isGiftCard
                            totalDiscountSet {
                                presentmentMoney {
                                    amount
                                    currencyCode
                                }
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
        }
    }
}

` $ordersLimit $customerId }}

{
    "query": {{toJson $query}}
}