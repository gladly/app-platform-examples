{{- $hasEmail := false -}}
{{- if .customer.emailAddresses -}}
{{- if gt (len .customer.emailAddresses) 0 -}}
{{- $hasEmail = true -}}
{{- end -}}
{{- end -}}

{{- if not $hasEmail -}}
{{- stop "No email addresses available for customer lookup" -}}
{{- end -}}
{{- range .customer.emailAddresses -}}
https://restapi.ordergroove.com/customers/?email={{urlquery .}}
{{ end -}}
