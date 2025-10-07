{{- if and .rawData .rawData.results -}}
{{- range $i, $subscription := .rawData.results -}}
{{- if $i }}
{{- end -}}
{{- if $subscription.updated -}}
{{- date "2006-01-02T15:04:05Z" (toDate "2006-01-02 15:04:05" $subscription.updated) -}}
{{- end -}}
{{- end -}}
{{- end -}}