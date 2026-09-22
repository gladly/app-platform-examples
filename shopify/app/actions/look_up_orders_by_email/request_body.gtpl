
{{- /* Retrieve orders by email. Note that only order's email will be matched. For example if a customer has an account with an 'customer@mail.com'
    and makes an order using 'order@mail.com', it will only be returned when searching for 'order@email.com'.
    If you want to lookup customers by email addresss, please use the Data Pull. 
    Orders are sorted by the most recently updated. By default first 40 orders are fetched. */ -}}
{{ $ol := .integration.configuration.ordersLimit}}
{{- $ordersLimit := $ol | default 10 -}}

{{-
$query := printf `
{
    orders(first: %.0f, sortKey: UPDATED_AT, reverse: true, query: "email:'%s'") {
        nodes {
            id
            customer {
                id
                displayName
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
            confirmationNumber
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
    }
}
` $ordersLimit .inputs.email
-}}

{
    "query": {{toJson $query}}
}