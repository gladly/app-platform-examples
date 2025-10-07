{{- $subscriptionId := regexReplaceAll ".*subscription=([^&]+).*" (printf "%s" .request.url) "${1}" -}}

{{- /* Check for API errors first */ -}}
{{- if and .response.statusCode (ne .response.statusCode 200) -}}
    {{- if .rawData.detail -}}
        {{- fail .rawData.detail -}}
    {{- else -}}
        {{- fail "API request failed" -}}
    {{- end -}}
{{- else if and .rawData .rawData.results -}}
[
{{- range $i, $order := .rawData.results -}}
{{- if $i -}},{{- end }}
{{- /* Transform date fields while preserving the rest of the order object */ -}}
{{- if $order.created -}}
    {{- $_ := set $order "created" ($order.created | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}
{{- end -}}
{{- if $order.updated -}}
    {{- $_ := set $order "updated" ($order.updated | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}
{{- end -}}
{{- if $order.place -}}
    {{- $_ := set $order "place" ($order.place | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}
{{- end -}}
{{- if and $order.cancelled (ne $order.cancelled "null") -}}
    {{- $_ := set $order "cancelled" ($order.cancelled | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}
{{- end -}}
{{- /* Add subscription info */ -}}
{{- if not $order.subscription -}}
    {{- if .request.url -}}
        {{- $_ := set $order "subscription" $subscriptionId -}}
    {{- end -}}
{{- end -}}
{{- $_ := set $order "subscriptionId" $subscriptionId -}}
{{toJson $order}}
{{- end -}}
]
{{- else -}}
[]
{{- end -}}