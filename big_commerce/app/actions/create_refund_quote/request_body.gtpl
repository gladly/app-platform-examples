{
  "items": [
    {
      "item_type": "ORDER",
      "item_id": {{- .inputs.orderId | int64 -}},
      "amount": {{- .inputs.amount -}}
    }
  ]
}