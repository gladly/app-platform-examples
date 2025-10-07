{{- /* Check for API errors first */ -}}
{{- if and .response.statusCode (ne .response.statusCode 200) -}}
    {{- if .rawData.detail -}}
        {{- fail .rawData.detail -}}
    {{- else -}}
        {{- fail "API request failed" -}}
    {{- end -}}
{{- else if and .rawData .rawData.results -}}
[
{{- range $index, $subscription := .rawData.results -}}
{{- if $index -}},{{- end }}
{{- /* Transform date fields while preserving the rest of the subscription object */ -}}
{{- if $subscription.start_date -}}
    {{- $_ := set $subscription "start_date" ($subscription.start_date | toDate "2006-01-02" | date "2006-01-02T15:04:05Z") -}}
{{- end -}}
{{- if $subscription.cancelled -}}
    {{- $_ := set $subscription "cancelled" ($subscription.cancelled | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}
{{- end -}}
{{- if $subscription.created -}}
    {{- $_ := set $subscription "created" ($subscription.created | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}
{{- end -}}
{{- if $subscription.updated -}}
    {{- $_ := set $subscription "updated" ($subscription.updated | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}
{{- end -}}
{{toJson $subscription}}
{{- end }}
]
{{- else -}}
[]
{{- end -}}