{{- /* 
    doc: https://developer.bigcommerce.com/docs/rest-management/transactions/payment-actions#create-a-refund 
*/ -}}

{{- $defaultRefundReason := "Sidekick Refund" -}}

{
    "items": [
        {
            "quantity": 1,
            "item_type": "ORDER",
            "reason": "{{- $defaultRefundReason -}}",
            "amount": {{- .inputs.amount -}},
            "item_id": {{- .inputs.orderId | int64 -}}
        }
    ],
    "payments": [
        {
            "offline": false,
            "amount": {{- .inputs.amount -}},
            "provider_id": "{{- .inputs.provider_id -}}"
        }
    ]
}
