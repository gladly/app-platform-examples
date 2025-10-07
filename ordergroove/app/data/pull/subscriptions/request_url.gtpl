{{- if .externalData.ordergroove_customer -}}
{{- range .externalData.ordergroove_customer -}}
https://restapi.ordergroove.com/subscriptions/?customer={{urlquery .id}}
{{ end -}}
{{- else -}}
{{- stop "Customer data is required to retrieve subscriptions" -}}
{{- end -}}