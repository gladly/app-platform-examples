{{- /* Identical body for skip and unskip -- only the URL verb (request_url.gtpl) differs. */ -}}
{{- $subscriptionId := .inputs.subscriptionId | int64 -}}

{
    "purchase_item_ids": [{{- $subscriptionId -}}]
}
