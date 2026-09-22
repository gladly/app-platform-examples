{
    "query": {{ toJson `mutation OrderUpdate($input: OrderInput!) {
        orderUpdate(input: $input) {
            order {
                id
                name
                updatedAt
                shippingAddress {
                    firstName
                    lastName
                    company
                    address1
                    address2
                    city
                    province
                    provinceCode
                    zip
                    country
                    countryCodeV2
                    phone
                    formatted(withName: true, withCompany: true)
                    validationResultSummary
                    coordinatesValidated
                    latitude
                    longitude
                }
            }
            userErrors {
                field
                message
            }
        }
    }`}},
    "variables": {
        "input": {
            "id": "{{ .inputs.orderId }}",
            {{- $shippingAddress := .inputs}}
            "shippingAddress": {
                {{ template "shippingAddressToProperties" $shippingAddress  }}
            }
        }
    }
}


{{- /* Converts input fields to Shopify's MailingAddressInput format */}}
{{- define "shippingAddressToProperties" -}}
    {{- $firstAttr := true }}

    {{- $getValue := dict
        "address1" .address1
        "address2" .address2
        "city" .city
        "company" .company
        "countryCode" .countryCode
        "firstName" .firstName
        "lastName" .lastName
        "phone" .phone
        "provinceCode" .provinceCode
        "zip" .zip
    }}

    {{- range $field, $value := $getValue }}
        {{- if and (not $firstAttr) (ne $value nil) (ne $value "") }},{{ end }}
        {{- if and (ne $value nil) (ne $value "") }}
            "{{ $field }}": "{{ $value }}"
            {{- $firstAttr = false }}
        {{- end }}
    {{- end }}
{{- end -}}