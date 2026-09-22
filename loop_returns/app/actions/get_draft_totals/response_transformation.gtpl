{{- /* Draft totals: surface amount due, exchange fee, and whether the price difference is covered */ -}}
{{ if and (ge .response.statusCode 200) (lt .response.statusCode 300) }}
{
    "amountDue": {{ if .rawData.data.amount_due }}{ "amount": {{ .rawData.data.amount_due.amount | default 0 }}, "currencyCode": {{ .rawData.data.amount_due.currencyCode | toJson }} }{{ else }}null{{ end }},
    "exchangeFee": {{ if and .rawData.data.fees .rawData.data.fees.exchange }}{ "amount": {{ .rawData.data.fees.exchange.amount | default 0 }}, "currencyCode": {{ .rawData.data.fees.exchange.currencyCode | toJson }} }{{ else }}null{{ end }},
    "isDifferenceCovered": {{ .rawData.data.is_difference_covered | default false }}
}
{{- else if eq .response.statusCode 401 -}}
    {{ "unauthorized: the Loop API token is missing a required scope (Draft Returns read+write, or Returns read)" | stop }}
{{- else if eq .response.statusCode 422 -}}
{
    "errors": [
        {{- if .rawData.errors -}}
        {{- range $i, $e := .rawData.errors }}{{if $i}},{{end}}
        { "code": {{ $e.code | toJson }}, "message": {{ coalesce $e.message $e.code "the request was rejected by Loop" | toJson }} }
        {{- end }}
        {{- else -}}
        { "message": {{ coalesce .rawData.message .rawData.error.message "the request was rejected by Loop" | toJson }} }
        {{- end }}
    ]
}
{{- else -}}
    {{ range .rawData.errors }}
        {{ printf "unable to execute action: '%s'" (coalesce .message .) | stop }}
    {{ else }}
        {{ printf "unable to execute action: '%s'" (coalesce .rawData.message .rawData.error.message "unexpected error from Loop") | stop }}
    {{ end }}
{{- end -}}
