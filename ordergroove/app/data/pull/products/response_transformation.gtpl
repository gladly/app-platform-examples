{{- /* Build set of external_product_ids returned by Ordergroove */ -}}
{{- $returnedIds := dict -}}
{{- if and .rawData .rawData.results -}}
    {{- range .rawData.results -}}
        {{- $_ := set $returnedIds .external_product_id true -}}
    {{- end -}}
{{- end -}}

{{- /* Collect IDs requested via items + subscriptions */ -}}
{{- $requestedIds := list -}}
{{- if .externalData.ordergroove_item -}}
    {{- range .externalData.ordergroove_item -}}
        {{- if .product -}}
            {{- $requestedIds = append $requestedIds .product -}}
        {{- end -}}
    {{- end -}}
{{- end -}}
{{- if .externalData.ordergroove_subscription -}}
    {{- range .externalData.ordergroove_subscription -}}
        {{- if .product -}}
            {{- $requestedIds = append $requestedIds .product -}}
        {{- end -}}
    {{- end -}}
{{- end -}}

{{- /* Orphan IDs: requested but not returned. Stubs satisfy @childIds so
       resolvers don't fail when Ordergroove omits a referenced product. */ -}}
{{- $missingIds := list -}}
{{- range ($requestedIds | uniq) -}}
    {{- if not (hasKey $returnedIds .) -}}
        {{- $missingIds = append $missingIds . -}}
    {{- end -}}
{{- end -}}

{{- if or (and .rawData .rawData.results) $missingIds -}}
[
{{- $first := true -}}
{{- if and .rawData .rawData.results -}}
    {{- range $product := .rawData.results -}}
        {{- if not $first -}},{{- end -}}
        {{- $first = false }}
{
  "id": "{{$product.external_product_id}}",
  "external_product_id": "{{$product.external_product_id}}",
  "name": "{{$product.name}}",
  "price": "{{$product.price}}",
  "sku": "{{$product.sku}}",
  "merchant": "{{$product.merchant}}",
  "live": {{$product.live}},
  "autoship_enabled": {{$product.autoship_enabled}}
}
    {{- end -}}
{{- end -}}
{{- range $id := $missingIds -}}
    {{- if not $first -}},{{- end -}}
    {{- $first = false }}
{
  "id": "{{$id}}",
  "external_product_id": "{{$id}}",
  "discontinued": true
}
{{- end }}
]
{{- else -}}
[]
{{- end -}}
