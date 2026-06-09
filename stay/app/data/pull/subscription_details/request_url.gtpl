{{- range .externalData.stay_subscription -}}
https://api.retextion.com/api/v2/subscriptions/{{.id}}
{{ end -}}
