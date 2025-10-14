{{- if eq .response.statusCode 200 -}}
    {{- $results := list -}}
    
    {{- /* Process each customer in the array */ -}}
    {{- range $customer := .rawData -}}
        {{- /* Create new result dict for this customer */ -}}
        {{- $result := dict -}}

        {{- /* Copy all fields from customer */ -}}
        {{- range $key, $value := $customer -}}
            {{- $_ := set $result $key $value -}}
        {{- end -}}

        {{- /* Transform customAttributes if present */ -}}
        {{- if $customer.customAttributes -}}
            {{- $customAttrs := list -}}
            {{- range $key, $value := $customer.customAttributes -}}
                {{- $customAttrs = append $customAttrs (dict "name" $key "value" $value) -}}
            {{- end -}}
            {{- $_ := set $result "customAttributes" $customAttrs -}}
        {{- end -}}

        {{- /* Transform externalCustomerIds if present */ -}}
        {{- if $customer.externalCustomerIds -}}
            {{- $externalIds := list -}}
            {{- range $key, $value := $customer.externalCustomerIds -}}
                {{- $externalIds = append $externalIds (dict "name" $key "value" $value) -}}
            {{- end -}}
            {{- $_ := set $result "externalCustomerIds" $externalIds -}}
        {{- end -}}

        {{- $results = append $results $result -}}
    {{- end -}}

    {{- toJson $results -}}
{{- else -}}
    {{- printf "Customer Lookup request failed with status code: %d" .response.statusCode | fail -}}
{{- end -}}