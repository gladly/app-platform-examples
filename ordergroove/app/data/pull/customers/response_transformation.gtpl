
{{- /* Check for API errors first */ -}}
{{- if and .response.statusCode (ne .response.statusCode 200) -}}
    {{- if .rawData.detail -}}
        {{- fail .rawData.detail -}}
    {{- else -}}
        {{- fail "API request failed" -}}
    {{- end -}}
{{- else if and .rawData .rawData.results -}}
[
{{- range $index, $customer := .rawData.results -}}
{{- if $index -}},{{- end }}
{{- /* Transform date fields while preserving the rest of the customer object */ -}}
{{- if $customer.created -}}
    {{- $_ := set $customer "created" ($customer.created | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}
{{- end -}}
{{- if $customer.last_updated -}}
    {{- $_ := set $customer "last_updated" ($customer.last_updated | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}
{{- end -}}
{{- if $customer.last_login -}}
    {{- $_ := set $customer "last_login" ($customer.last_login | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}
{{- end -}}
{{- /* Rename merchant_user_id to id for consistency */ -}}
{{- if $customer.merchant_user_id -}}
    {{- $_ := set $customer "id" $customer.merchant_user_id -}}
    {{- $_ := unset $customer "merchant_user_id" -}}
{{- end -}}
{{- /* Convert empty strings to null */ -}}
{{- if eq $customer.user_token_id "" -}}
    {{- $_ := set $customer "user_token_id" nil -}}
{{- end -}}
{{toJson $customer}}
{{- end }}
]
{{- else -}}
[]
{{- end -}}