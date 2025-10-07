{{- /* Check for API errors first */ -}}
{{- if and .response.statusCode (ne .response.statusCode 200) -}}
    {{- if .rawData.detail -}}
        {{- fail .rawData.detail -}}
    {{- else -}}
        {{- fail "API request failed" -}}
    {{- end -}}
{{- else if and .rawData .rawData.results -}}
[
{{- range $i, $item := .rawData.results -}}
{{- if $i -}},{{- end }}
{{- /* Transform date fields while preserving the rest of the item object */ -}}
{{- if $item.order_updated -}}
    {{- $_ := set $item "order_updated" ($item.order_updated | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}
{{- end -}}
{{- if and $item.first_placed (ne $item.first_placed "null") (ne $item.first_placed "") -}}
    {{- $_ := set $item "first_placed" ($item.first_placed | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}
{{- end -}}
{{- /* Convert empty strings to null */ -}}
{{- if eq $item.product_attribute "" -}}
    {{- $_ := set $item "product_attribute" nil -}}
{{- end -}}
{{- if and $item.components (eq (len $item.components) 0) -}}
    {{- $_ := set $item "components" nil -}}
{{- end -}}
{{- if eq $item.subscription_component "" -}}
    {{- $_ := set $item "subscription_component" nil -}}
{{- end -}}
{{- if eq $item.extra_cost "" -}}
    {{- $_ := set $item "extra_cost" nil -}}
{{- end -}}
{{toJson $item}}
{{- end -}}
]
{{- else -}}
[]
{{- end -}}