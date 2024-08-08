{
    "lookupLevel": "BASIC",
    "query": {
        "id": "{{.customer.id}}",
        "name": "{{.customer.name}}",
        "emails": [{{template "emailAddresses" .customer}}],
        "phones": [{{template "phoneNumbers" .customer}}]
    },
    "uniqueMatchRequired": false
}

{{- define "emailAddresses"}}
    {{- if .primaryEmailAddress -}}
        "{{.primaryEmailAddress}}"{{if gt (len .emailAddresses) 1}},{{end -}}
    {{- end}}
    {{- $nonPrimaries := without .emailAddresses .primaryEmailAddress -}}
    {{- range $index, $emailAddress := $nonPrimaries}}{{if gt $index 0}},{{end -}}
        "{{$emailAddress}}"
    {{- end}}
{{- end}}

{{- define "phoneNumbers"}}
    {{- range $index, $phoneNumber := .phoneNumbers}}{{if gt $index 0}},{{end -}}
        "{{$phoneNumber.number}}"
    {{- end}}
{{- end}}