{{if eq .response.statusCode 200}}

    {{- /* Create new result dict */ -}}
    {{- $result := dict -}}

    {{- /* Copy all fields from rawData */ -}}
    {{- range $key, $value := .rawData -}}
      {{- $_ := set $result $key $value -}}
    {{- end -}}

    {{- /* Now override the specific fields */ -}}
    {{- if .rawData.customAttributes -}}
      {{- $customAttrs := list -}}
      {{- range $key, $value := .rawData.customAttributes -}}
        {{- $customAttrs = append $customAttrs (dict "name" $key "value" $value) -}}
      {{- end -}}
      {{- $_ := set $result "customAttributes" $customAttrs -}}
    {{- end -}}

    {{- if .rawData.externalCustomerIds -}}
      {{- $externalIds := list -}}
      {{- range $key, $value := .rawData.externalCustomerIds -}}
        {{- $externalIds = append $externalIds (dict "name" $key "value" $value) -}}
      {{- end -}}
      {{- $_ := set $result "externalCustomerIds" $externalIds -}}
    {{- end -}}

    {{toJson $result}}
{{- else}}
    {{- printf "Customer Lookup request failed with status code: %d" .response.statusCode | fail}}
{{- end}}