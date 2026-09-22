{{- $input := dict -}}
{{- if and (ne .inputs.subscriptionLineId nil) (ne (.inputs.subscriptionLineId | toString | trim) "") -}}{{- $input = set $input "subscriptionLineId" (.inputs.subscriptionLineId | toString) -}}{{- end -}}
{{- if and (ne .inputs.prepaidSubscriptionLineId nil) (ne (.inputs.prepaidSubscriptionLineId | toString | trim) "") -}}{{- $input = set $input "prepaidSubscriptionLineId" (.inputs.prepaidSubscriptionLineId | toString) -}}{{- end -}}
{{- if ne .inputs.quantity nil -}}{{- $input = set $input "quantity" (.inputs.quantity | int) -}}{{- end -}}
{{- if and (ne .inputs.productVariantId nil) (ne (.inputs.productVariantId | toString | trim) "") -}}{{- $input = set $input "productVariantId" (.inputs.productVariantId | toString) -}}{{- end -}}
{{- if ne .inputs.currentPrice nil -}}{{- $input = set $input "currentPrice" (.inputs.currentPrice | float64) -}}{{- end -}}
{{- if and (ne .inputs.titleOverride nil) (ne (.inputs.titleOverride | toString | trim) "") -}}{{- $input = set $input "titleOverride" (.inputs.titleOverride | toString) -}}{{- end -}}
{
  "query": "mutation updateSubscriptionLine($input: UpdateSubscriptionLineInput!) { updateSubscriptionLine(input: $input) { ok } }",
  "variables": {
    "input": {{ toJson $input }}
  }
}
