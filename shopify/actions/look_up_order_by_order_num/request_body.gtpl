{{ if or (eq .inputs.orderNum nil) (eq .inputs.orderNum "") }}
	{{ stop "Input OrderNum is required."}}
{{ else }}
{{$query := printf `
{
    orders(first: 1, query: "name:'%s'") {
        edges {
            node {
                id
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
                shippingLines(first: 40) {
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
    ` .inputs.orderNum}}

{
    "query": {{toJson $query}}
}

{{ end }}