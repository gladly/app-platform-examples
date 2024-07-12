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
    {{- range $index, $emailAddress := .emailAddresses}}{{if gt $index 0}},{{end -}}
        "{{$emailAddress}}"
    {{- end}}
{{- end}}

{{- define "phoneNumbers"}}
    {{- range $index, $phoneNumber := .phoneNumbers}}{{if gt $index 0}},{{end -}}
        "{{$phoneNumber.number}}"
    {{- end}}
{{- end}}