{{- /* Recharge wants a `free_gifts` ARRAY of gift objects, not top-level fields. We add one gift. */ -}}
{{- $quantity := .inputs.quantity | int64 -}}
{{- $gift := dict "external_variant_id" .inputs.externalVariantId "quantity" $quantity -}}
{{- if and (ne .inputs.price nil) (ne .inputs.price "") -}}{{- $gift = set $gift "price" .inputs.price -}}{{- end -}}

{
    "free_gifts": [{{ toJson $gift }}]
}
