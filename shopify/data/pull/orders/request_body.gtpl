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
        nodes {
            id
            customer {
                id
                displayName
            }            
            confirmationNumber
            customer {
                id
            }
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
            shippingLines(first: 10) {
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
    }
}

` $ordersLimit $customerId }}

{
    "query": {{toJson $query}}
}