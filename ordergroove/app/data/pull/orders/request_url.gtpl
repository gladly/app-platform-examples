{{- if and .externalData.ordergroove_subscription (gt (len .externalData.ordergroove_subscription) 0) -}}
{{- range .externalData.ordergroove_subscription -}}
https://restapi.ordergroove.com/orders/?subscription={{urlquery .public_id}}&page_size=100
{{ end -}}
{{- end -}}