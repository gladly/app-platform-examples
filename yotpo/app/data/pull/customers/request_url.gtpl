{{- $hasEmailOrMobilePhone := false -}}
{{- if .customer.emailAddresses -}}
{{- if gt (len .customer.emailAddresses) 0 -}}
{{- $hasEmailOrMobilePhone = true -}}
{{- end -}}
{{- end -}}
{{- if .customer.phoneNumbers -}}
{{- range .customer.phoneNumbers -}}
{{- if eq .type "MOBILE" -}}
{{- $hasEmailOrMobilePhone = true -}}
{{- end -}}
{{- end -}}
{{- end -}}
{{- if not $hasEmailOrMobilePhone -}}
{{- stop "No email addresses or mobile phone numbers available for customer lookup" -}}
{{- end -}}
{{- range .customer.emailAddresses -}}
https://developers.yotpo.com/core/v3/stores/{{$.integration.configuration.store_id}}/customers?email={{urlquery .}}&limit=5&access_token={{$.integration.secrets.access_token}}&expand=reviews
{{ end -}}
{{- range .customer.phoneNumbers -}}
{{- if eq .type "MOBILE" -}}
https://developers.yotpo.com/core/v3/stores/{{$.integration.configuration.store_id}}/customers?phone_number={{urlquery .number}}&limit=5&access_token={{$.integration.secrets.access_token}}&expand=reviews
{{ end -}}
{{- end -}}
