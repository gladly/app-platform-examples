{{- $input := dict "subscriptionId" (.inputs.subscriptionId | toString) -}}
{{- if ne .inputs.deliveryPrice nil -}}{{- $input = set $input "deliveryPrice" (.inputs.deliveryPrice | float64) -}}{{- end -}}
{
  "query": "mutation setDeliveryPriceOverride($input: SetDeliveryPriceOverrideInput!) { setDeliveryPriceOverride(input: $input) { subscriptionId } }",
  "variables": {
    "input": {{ toJson $input }}
  }
}
