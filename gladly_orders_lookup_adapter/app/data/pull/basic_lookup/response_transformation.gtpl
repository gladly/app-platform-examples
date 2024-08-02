{{- /* the lookup adaptor did not return any results */}}
{{- if or (not .rawData.results) (eq (len .rawData.results) 0) }}
    {{stop "the lookup adaptor did not return a customer"}}
{{- end}}

{{- /* can only auto-link if the lookup adaptor returns 1 customer */}}
{{- if ne (len .rawData.results) 1}}
    {{stop "unable to auto-link since lookup adaptor returned more than one customer"}}
{{- end}}

{{- $customer := .customer}}
{{- $externalCustomer := index .rawData.results 0}}
{{- $customerMatch := false}}

{{- /* returned email addresses must match one of the Gladly customer profile email addresses if "emails" matching has been configured */}}
{{- if has "emails" .integration.configuration.autoLinkFields}}
    {{- range $externalCustomer.emails}}
        {{- if has (trim (lower .original)) $customer.emailAddresses}}
            {{- $customerMatch = true}}
            {{- break}}
        {{- end }}
    {{- end}}
{{- end}}

{{- /* returned mobile phone numbers must match one of the Gladly customer profile mobile phone numbers if "phones.mobile" matching has been configured */}}
{{- if has "phones.mobile" .integration.configuration.autoLinkFields}}
    {{- range $externalCustomer.phones}}
        {{- if ne .type "MOBILE"}}
            {{- continue}}
        {{- end }}
        {{- $externalCustomerPhoneNumber := .original}}
        {{- /* find the external customer mobile phone number in the Gladly customer profile */}}
        {{- range $customer.phoneNumbers}}
            {{- if and (eq .number $externalCustomerPhoneNumber) (eq .type "MOBILE")}}
                {{- $customerMatch = true}}
            {{- end}}
        {{- end}}
    {{- end}}
{{- end}}

{{- if not $customerMatch}}
    {{stop "unable to auto-link since the Gladly customer profile does not contain any matching email addresses or mobile phone numbers returned by the lookup adaptor"}}
{{- end}}

{{- with $externalCustomer -}}
{
    "id": "{{.externalCustomerId}}",
    "name": "{{.name}}",
    "emailAddresses": [
        {{- range $index, $email := .emails}}{{if gt $index 0}},{{end}}
        "{{.original}}"
        {{- end}}
    ],
    "phoneNumbers": [
        {{- range $index, $phone := .phones}}{{if gt $index 0}},{{end}}
        {
            "number": "{{.original}}",
            "type": "{{.type}}"
        }
        {{- end}}
    ],
    "customAttributes": [
        {{- $firstElement := true}}
        {{- range $name, $value := .customAttributes}}{{if not $firstElement}},{{end}}
        {
            "name": "{{$name}}",
            "value": "{{$value}}"
        }
        {{- $firstElement = false}}
        {{end}}
    ]
}
{{end}}