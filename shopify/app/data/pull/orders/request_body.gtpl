{{- /*Customer orders are sorted by the most recently updated. By default first 40 orders are fetched.
    Only orders belonging to a customer matched in customer data pull are currently retrieved, i.e. if
    customer created an order without an account it will not be returned. */ -}}
{{$externalCustomer := index .externalData.shopify_customer 0}}


{{- $ordersLimit := 40 -}}
{{- with index .integration.configuration "ordersLimit" -}}
{{- $parsed := atoi (printf "%v" .) -}}
{{- if le $parsed 0 }}{{ fail (printf "Shopify 'ordersLimit' must be a positive whole number, got %v. Recent orders cannot be retrieved for this customer." .) }}{{ end -}}
{{- $ordersLimit = $parsed -}}
{{- end -}}

{{- $customerId := splitList "/" $externalCustomer.id | last -}}

{{- /* Same limit the metafields data pull uses, so hasNextPage reports the cap
    that will actually be applied to each order's metafields. */ -}}
{{- $metafieldsLimit := (default 50 (index .integration.configuration "metafieldsLimit")) -}}

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
            currentShippingPriceSet {
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
            currentTotalDutiesSet {
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
            taxesIncluded
            totalReceivedSet {
                shopMoney {
                    amount
                    currencyCode
                }
                presentmentMoney {
                    amount
                    currencyCode
                }
            }
            totalRefundedSet {
                shopMoney {
                    amount
                    currencyCode
                }
                presentmentMoney {
                    amount
                    currencyCode
                }
            }
            totalOutstandingSet {
                shopMoney {
                    amount
                    currencyCode
                }
                presentmentMoney {
                    amount
                    currencyCode
                }
            }
            refundable
            displayFinancialStatus
            displayFulfillmentStatus
            returnStatus
            sourceName
            app {
                name
            }
            shippingAddress {
                firstName
                lastName
                company
                address1
                address2
                city
                province
                provinceCode
                zip
                country
                countryCodeV2
                phone
                formatted(withName: true, withCompany: true)
                validationResultSummary
                coordinatesValidated
                latitude
                longitude
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
                    currentQuantity
                    unfulfilledQuantity
                    sellingPlan {
                        name
                        sellingPlanId
                    }
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
            metafields(first: %v) {
                pageInfo {
                    hasNextPage
                }
            }
        }
    }
}
` $ordersLimit $customerId $metafieldsLimit }}

{
    "query": {{toJson $query}}
}
