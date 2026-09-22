{{- $input := dict "subscriptionId" (.inputs.subscriptionId | toString) "oldProductVariantId" (.inputs.oldProductVariantId | toString) "newProductVariantId" (.inputs.newProductVariantId | toString) -}}
{{- if and (ne .inputs.pricingOption nil) (ne (.inputs.pricingOption | toString | trim) "") -}}{{- $input = set $input "pricingOption" (.inputs.pricingOption | toString | trim | upper) -}}{{- end -}}
{{- if ne .inputs.isGiftSwap nil -}}{{- $input = set $input "isGiftSwap" (.inputs.isGiftSwap) -}}{{- end -}}
{{- /* newCustomPrice is a String on Skio's input, not a Float, so absent and empty-after-trim
       have to be told apart the same way request_url.gtpl tells them apart: a cleared form
       field arrives as "", which is not nil, and sending "" as a price is not what the agent
       asked for. Drop the key unless a real price was typed. */ -}}
{{- if ne .inputs.newCustomPrice nil -}}
    {{- $newCustomPrice := .inputs.newCustomPrice | toString | trim -}}
    {{- if ne $newCustomPrice "" -}}
        {{- $input = set $input "newCustomPrice" $newCustomPrice -}}
    {{- end -}}
{{- end -}}
{{- /* Restrict the swap to specific lines; omitted means every line on that variant. */ -}}
{{- if and (ne .inputs.subscriptionLinesToSwapFrom nil) (gt (len .inputs.subscriptionLinesToSwapFrom) 0) -}}
    {{- $lines := list -}}
    {{- range $id := .inputs.subscriptionLinesToSwapFrom -}}
        {{- $lines = append $lines ($id | toString) -}}
    {{- end -}}
    {{- $input = set $input "subscriptionLinesToSwapFrom" $lines -}}
{{- end -}}
{
  "query": "mutation swapSubscriptionProductVariants($input: SwapSubscriptionProductVariantsInput!) { swapSubscriptionProductVariants(input: $input) { ok } }",
  "variables": {
    "input": {{ toJson $input }}
  }
}
