{{- /* Map each declared item onto Skio's AddSubscriptionLineInput. Skio wants the
       subscription id on every item; callers give it once. */ -}}
{{- $items := list -}}
{{- range $item := .inputs.items -}}
    {{- $line := dict "subscriptionId" ($.inputs.subscriptionId | toString) "variantId" ($item.variantId | toString) -}}
    {{- if ne $item.quantity nil -}}{{- $line = set $line "quantity" ($item.quantity | int) -}}{{- end -}}
    {{- if ne $item.price nil -}}{{- $line = set $line "price" ($item.price | float64) -}}{{- end -}}
    {{- if ne $item.upsell nil -}}{{- $line = set $line "upsell" $item.upsell -}}{{- end -}}
    {{- $items = append $items $line -}}
{{- end -}}
{{- if eq (len $items) 0 -}}{{- stop "No products were supplied. Add at least one product to the subscription." -}}{{- end -}}
{{- $input := dict "items" $items -}}
{
  "query": "mutation addSubscriptionLineBulk($input: AddSubscriptionLineBulkInput!) { addSubscriptionLineBulk(input: $input) { ok } }",
  "variables": {
    "input": {{ toJson $input }}
  }
}
