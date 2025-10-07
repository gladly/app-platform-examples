{{- /* Collect all unique product IDs from items */ -}}
{{- $productIds := list -}}
{{- if .externalData.ordergroove_item -}}
{{- range .externalData.ordergroove_item -}}
{{- if .product -}}
{{- $productIds = append $productIds .product -}}
{{- end -}}
{{- end -}}
{{- end -}}
{{- /* Remove duplicates by converting to unique set */ -}}
{{- $uniqueProductIds := $productIds | uniq -}}
{{- /* Build URL with product IDs as filter or fetch all if none */ -}}
{{- if $uniqueProductIds -}}
https://restapi.ordergroove.com/products/?external_product_id__in={{- range $i, $id := $uniqueProductIds -}}{{- if $i -}},{{- end -}}{{- urlquery $id -}}{{- end -}}
{{- else -}}
https://restapi.ordergroove.com/products/
{{- end -}}