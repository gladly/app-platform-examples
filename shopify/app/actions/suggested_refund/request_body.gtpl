{{ $query := `
query suggestedRefund($orderId: ID!, $refundLineItems: [RefundLineItemInput!]) {
  order(id: $orderId) {
    suggestedRefund(
      refundLineItems: $refundLineItems
    ) {
        discountedSubtotalSet {
            shopMoney {amount currencyCode}
        }
        maximumRefundableSet {
            shopMoney {amount currencyCode}
        }
        amountSet {
            shopMoney {amount currencyCode}
        }
        totalCartDiscountAmountSet {
            shopMoney {amount currencyCode}
        }
        totalDutiesSet {
            shopMoney {amount currencyCode}
        }
        totalTaxSet {
            shopMoney {amount currencyCode}
        }
        refundDuties {
            amountSet {
                shopMoney {amount currencyCode}
            }
            originalDuty {
                id
                countryCodeOfOrigin
                harmonizedSystemCode
                price {
                    shopMoney {amount currencyCode}
                }
                taxLines {
                    channelLiable
                    priceSet {
                        shopMoney {amount currencyCode}
                    }
                    rate
                    ratePercentage
                    source
                    title
                }
            }
        }
        refundLineItems {
            id
            quantity
            restockType
            lineItem {
                id
            }
            priceSet {
                shopMoney {
                    amount
                    currencyCode
                }
            }
            restocked
            subtotalSet {
                shopMoney {
                    amount
                    currencyCode
                }
            }
            totalTaxSet {
                shopMoney {
                    amount
                    currencyCode
                }
            }   
        }
        shipping {
            amountSet {
                shopMoney {amount currencyCode}
            }
            maximumRefundableSet {
                shopMoney {amount currencyCode}
            }
            taxSet {
                shopMoney {amount currencyCode}
            }
        }
        subtotalSet {
            shopMoney {amount currencyCode}
        }
        suggestedTransactions {
            accountNumber
            amountSet {
                shopMoney {amount currencyCode}
            }
            formattedGateway 
            gateway
            kind
            maximumRefundableSet {
                shopMoney {amount currencyCode}
            }
            maximumRefundableSet {
                shopMoney {amount currencyCode}
            }
            parentTransaction {
                id
            }
            paymentDetails {
                __typename
            }
        }
    }
  }
}

` }}

{
    "query": {{ toJson $query }},
    "variables": {
        "orderId": "{{ .inputs.orderId }}",
        "refundLineItems": [{
            "lineItemId": "{{ .inputs.lineItemId }}",
            "quantity": {{ .inputs.quantity }}
        }]
    }
}