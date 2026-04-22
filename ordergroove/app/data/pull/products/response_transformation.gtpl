{{- if .rawData.results -}}
[
{{- range $i, $product := .rawData.results -}}
{{- if $i -}},{{- end }}
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
{{- end }}
]
{{- else -}}
[]
{{- end -}}