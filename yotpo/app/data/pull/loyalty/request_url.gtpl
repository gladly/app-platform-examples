{{- if or (not .integration.configuration.store_id) (not .integration.secrets.access_token) -}}
{{- stop "store_id and access_token cannot be empty" -}}
{{- end -}}
https://developers.yotpo.com/loyalty/v3/stores/{{.integration.configuration.store_id}}/customers?access_token={{.integration.secrets.access_token}}
