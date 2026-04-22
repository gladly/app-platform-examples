{{- /* Collect all unique product IDs from items */ -}}
{{- $productIds := list -}}
{{- if .externalData.ordergroove_item -}}
    {{- range .externalData.ordergroove_item -}}
        {{- if .product -}}
            {{- $productIds = append $productIds .product -}}
        {{- end -}}
    {{- end -}}
{{- end -}}

{{- if .externalData.ordergroove_subscription -}}
    {{- range .externalData.ordergroove_subscription -}}
        {{- if .product -}}
            {{- $productIds = append $productIds .product -}}
        {{- end -}}
    {{- end -}}
{{- end -}}

{{- /* Remove duplicates by converting to unique set */ -}}
{{- $uniqueProductIds := $productIds | uniq -}}
{{- /* Build URL with product IDs as filter or fetch all if none */ -}}
{{- if $uniqueProductIds -}}
https://restapi.ordergroove.com/products/?external_product_ids[]={{- join "&external_product_ids[]=" $uniqueProductIds -}}
{{- else -}}
https://restapi.ordergroove.com/products/
{{- end -}}
