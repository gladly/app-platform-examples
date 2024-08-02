{{$externalCustomer := index .externalData.gladly_customer 0}}
{
    "lookupLevel": "DETAILED",
    "query": {
        "id": "{{.customer.id}}",
        "externalCustomerId": "{{$externalCustomer.id}}",
        "name": "{{.customer.name}}",
        "emails": [{{template "emailAddresses" .customer}}],
        "phones": [{{template "phoneNumbers" .customer}}],
        "customAttributes": {
            {{- range $index, $customAttribute := $externalCustomer.customAttributes}}{{if gt $index 0}},{{end}}
            "{{$customAttribute.name}}": "{{$customAttribute.value}}"
            {{- end}}
        }
    },
    "uniqueMatchRequired": true
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