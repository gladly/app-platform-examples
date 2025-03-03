
{{ $query := printf `
mutation M($input: RefundInput!) {
    refundCreate(input: $input) {
        userErrors {
            field
            message
        }
        refund {
            id
            note
            createdAt
            refundLineItems(first: 10) {
                nodes {
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
            }
            transactions(first: 10) {
                nodes {
                        id
                        order {
                            id
                        }
                        kind
                        gateway
                        parentTransaction {
                            id
                        }
                        maximumRefundableV2 {
                            amount
                        }
                        amountSet {
                            shopMoney {
                                amount
                                currencyCode
                            }
                        }
                        status
                        errorCode
                }
            }
            orderAdjustments(first: 10) {
                nodes {
                        id
                        amountSet {
                            shopMoney {
                                amount
                                currencyCode
                            }
                        }
                        taxAmountSet {
                            shopMoney {
                                amount
                                currencyCode
                            }
                        }
                        reason
                }
            }
            totalRefundedSet {
                shopMoney { amount currencyCode }
            }
        }
    }
}
` 
}}

{
    "query": {{ toJson $query }},
    "variables": {
        "input": {
            "note": {{ toJson .inputs.note }},
            {{- if .inputs.notify }} 
            "notify": {{ toJson .inputs.notify }},
            {{- end }}
            "refundLineItems": [{
                "lineItemId": "{{ .inputs.lineItemId }}",
                "quantity": {{ .inputs.quantity }}
            }], 
            "transactions": [{
                    "orderId": "{{ .inputs.orderId }}",
                    "amount": {{ .inputs.transactionAmount }},
                    "gateway": "{{ .inputs.transactionGateway }}",
                    "kind": "{{ .inputs.transactionKind }}",
                    "parentId": "{{ .inputs.transactionParentId }}"
            }],
            "orderId": "{{ .inputs.orderId }}"
        }
    }
}