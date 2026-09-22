{{- /* Map each declared item onto Skio's UpdateSubscriptionLineInput. A line is keyed by
       subscriptionLineId (or prepaidSubscriptionLineId) - NOT by subscriptionId. */ -}}
{{- $items := list -}}
{{- range $item := .inputs.items -}}
    {{- $line := dict -}}
    {{- if and (ne $item.subscriptionLineId nil) (ne ($item.subscriptionLineId | toString | trim) "") -}}
        {{- $line = set $line "subscriptionLineId" ($item.subscriptionLineId | toString) -}}
    {{- end -}}
    {{- if and (ne $item.prepaidSubscriptionLineId nil) (ne ($item.prepaidSubscriptionLineId | toString | trim) "") -}}
        {{- $line = set $line "prepaidSubscriptionLineId" ($item.prepaidSubscriptionLineId | toString) -}}
    {{- end -}}
    {{- if eq (len $line) 0 -}}
        {{- stop "Every line to update needs a subscription line id (or a prepaid subscription line id)." -}}
    {{- end -}}
    {{- if ne $item.quantity nil -}}{{- $line = set $line "quantity" ($item.quantity | int) -}}{{- end -}}
    {{- if and (ne $item.productVariantId nil) (ne ($item.productVariantId | toString | trim) "") -}}
        {{- $line = set $line "productVariantId" ($item.productVariantId | toString) -}}
    {{- end -}}
    {{- if ne $item.currentPrice nil -}}{{- $line = set $line "currentPrice" ($item.currentPrice | float64) -}}{{- end -}}
    {{- if and (ne $item.titleOverride nil) (ne ($item.titleOverride | toString | trim) "") -}}
        {{- $line = set $line "titleOverride" ($item.titleOverride | toString) -}}
    {{- end -}}
    {{- $items = append $items $line -}}
{{- end -}}
{{- if eq (len $items) 0 -}}{{- stop "No lines were supplied. Add at least one line to update." -}}{{- end -}}
{{- $input := dict "items" $items -}}
{
  "query": "mutation updateSubscriptionLineBulk($input: UpdateSubscriptionLineBulkInput!) { updateSubscriptionLineBulk(input: $input) { ok } }",
  "variables": {
    "input": {{ toJson $input }}
  }
}
