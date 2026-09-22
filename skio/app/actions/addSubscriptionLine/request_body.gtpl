{{- $input := dict "subscriptionId" (.inputs.subscriptionId | toString) "variantId" (.inputs.variantId | toString) -}}
{{- if ne .inputs.quantity nil -}}{{- $input = set $input "quantity" (.inputs.quantity | int) -}}{{- end -}}
{{- if ne .inputs.price nil -}}{{- $input = set $input "price" (.inputs.price | float64) -}}{{- end -}}
{{- if ne .inputs.upsell nil -}}{{- $input = set $input "upsell" (.inputs.upsell) -}}{{- end -}}
{
  "query": "mutation addSubscriptionLine($input: AddSubscriptionLineInput!) { addSubscriptionLine(input: $input) { ok } }",
  "variables": {
    "input": {{ toJson $input }}
  }
}
