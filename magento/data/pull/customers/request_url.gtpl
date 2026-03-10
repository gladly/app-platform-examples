{{- if not .customer.emailAddresses -}}
  {{- stop "no customer email addresses available" -}}
{{- end -}}
{{- range .customer.emailAddresses -}}
https://{{$.integration.configuration.host}}/rest/V1/customers/search?searchCriteria[filter_groups][0][filters][0][field]=email&searchCriteria[filter_groups][0][filters][0][value]={{urlquery .}}&searchCriteria[filter_groups][0][filters][0][condition_type]=eq
{{ end -}}