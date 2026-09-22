{{- if or (not .integration.configuration.store_id) (not .integration.secrets.access_token) -}}
{{- stop "store_id and access_token cannot be empty" -}}
{{- end -}}
{{- /* Customers with no email or mobile phone produce no request URLs: the pull
       succeeds with no data instead of erroring the whole Yotpo refresh. */ -}}
{{- range .customer.emailAddresses -}}
https://developers.yotpo.com/core/v3/stores/{{$.integration.configuration.store_id}}/customers?email={{urlquery .}}&limit=5&access_token={{$.integration.secrets.access_token}}&expand=reviews
{{ end -}}
{{- range .customer.phoneNumbers -}}
{{- if eq .type "MOBILE" -}}
https://developers.yotpo.com/core/v3/stores/{{$.integration.configuration.store_id}}/customers?phone_number={{urlquery .number}}&limit=5&access_token={{$.integration.secrets.access_token}}&expand=reviews
{{ end -}}
{{- end -}}
