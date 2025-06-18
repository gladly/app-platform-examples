{{- $subscriptionId := .inputs.subscriptionId | int64 -}}

{
    "purchase_item_ids": [{{- $subscriptionId -}}]
}
