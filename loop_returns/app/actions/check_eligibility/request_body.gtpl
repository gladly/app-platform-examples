{
    "order_name": {{.inputs.orderName | toJson}},
    "secondary_input": {{.inputs.secondaryInput | toJson}},
    "is_gift": {{.inputs.isGift | default false}}
}