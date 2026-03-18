{{- range .externalData.customer -}}
{{- if .email -}}
#body
{
    "customers": {
        "customer_email": "{{.email}}"
    },
    "with_referral_code": true
}
{{ end -}}
{{- end -}}
