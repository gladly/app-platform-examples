[
{{- range $index, $item := .rawData.results -}}
{{- if $index -}},{{- end }}
{
  "offer": {{if $item.offer}}"{{$item.offer}}"{{else}}null{{end}},
  "order": {{if $item.order}}"{{$item.order}}"{{else}}null{{end}},
  "subscription": {{if $item.subscription}}"{{$item.subscription}}"{{else}}null{{end}},
  "product": {{if $item.product}}"{{$item.product}}"{{else}}null{{end}},
  "components": {{if $item.components}}"{{$item.components}}"{{else}}null{{end}},
  "subscription_component": {{if $item.subscription_component}}"{{$item.subscription_component}}"{{else}}null{{end}},
  "quantity": {{if $item.quantity}}{{$item.quantity}}{{else}}null{{end}},
  "public_id": {{if $item.public_id}}"{{$item.public_id}}"{{else}}null{{end}},
  "product_attribute": {{if $item.product_attribute}}"{{$item.product_attribute}}"{{else}}null{{end}},
  "price": {{if $item.price}}"{{$item.price}}"{{else}}null{{end}},
  "extra_cost": {{if $item.extra_cost}}"{{$item.extra_cost}}"{{else}}null{{end}},
  "total_price": {{if $item.total_price}}"{{$item.total_price}}"{{else}}null{{end}},
  "one_time": {{if ne $item.one_time nil}}{{$item.one_time}}{{else}}null{{end}},
  "order_updated": {{if $item.order_updated}}"{{date "2006-01-02T15:04:05Z" (toDate "2006-01-02 15:04:05" $item.order_updated)}}"{{else}}null{{end}},
  "frozen": {{if ne $item.frozen nil}}{{$item.frozen}}{{else}}null{{end}},
  "first_placed": {{if $item.first_placed}}"{{date "2006-01-02T15:04:05Z" (toDate "2006-01-02 15:04:05" $item.first_placed)}}"{{else}}null{{end}}
}
{{- end }}
]
