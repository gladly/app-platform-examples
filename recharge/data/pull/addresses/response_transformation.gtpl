{{- $hasAddresses := and (ne .rawData nil) (ne .rawData.addresses nil) (gt (len .rawData.addresses) 0) -}}

{{- if $hasAddresses -}}
[
    {{- range $index, $address := .rawData.addresses -}}

        {{- /* create a map with formatted address IDs as strings to ensure proper graphql output formatting */ -}}
        {{- $formattedAdressFields := dict 
            "id" ($address.id | int64 | toString) 
            "customer_id" ($address.customer_id | int64 | toString) 
            "payment_method_id" ($address.payment_method_id | int64 | toString) 
        -}}

        {{- /* merge the formatted IDs back into the address data */ -}}
        {{- $address = mergeOverwrite $address $formattedAdressFields -}}

        {{- $address | toJson -}}

        {{- if lt (add $index 1) (len $.rawData.addresses) -}},{{- end -}}

    {{- end -}}
]
{{- else -}}
[]
{{- end -}}
