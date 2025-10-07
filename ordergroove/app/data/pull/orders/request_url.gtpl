{{- if .externalData.ordergroove_subscription -}}
{{- range .externalData.ordergroove_subscription -}}
https://restapi.ordergroove.com/orders/?subscription={{urlquery .public_id}}
{{ end -}}
{{- else -}}
{{- stop "Subscription data is required to retrieve orders" -}}
{{- end -}}