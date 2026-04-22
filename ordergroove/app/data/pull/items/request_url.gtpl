{{- if and .externalData.ordergroove_order (gt (len .externalData.ordergroove_order) 0) -}}
{{- range .externalData.ordergroove_order -}}
https://restapi.ordergroove.com/items/?order={{urlquery .public_id}}
{{ end -}}
{{- end -}}