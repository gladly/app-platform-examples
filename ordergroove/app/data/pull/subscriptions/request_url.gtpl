{{- if and .externalData.ordergroove_customer (gt (len .externalData.ordergroove_customer) 0) -}}
{{- range .externalData.ordergroove_customer -}}
https://restapi.ordergroove.com/subscriptions/?customer={{urlquery .id}}
{{ end -}}
{{- end -}}