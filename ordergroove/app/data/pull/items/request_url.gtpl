{{- if .externalData.ordergroove_order -}}
{{- range .externalData.ordergroove_order -}}
https://restapi.ordergroove.com/items/?order={{urlquery .public_id}}
{{ end -}}
{{- else -}}
{{- stop "Order data is required to retrieve items" -}}
{{- end -}}