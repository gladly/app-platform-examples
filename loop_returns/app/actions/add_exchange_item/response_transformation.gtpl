{{- /* Handle non-success status codes with a clear, agent-readable message */ -}}
{{ if and (ge .response.statusCode 200) (lt .response.statusCode 300) }}
{{- /* The added exchange variant lives in draft_return.cart_items; match on the returning item */ -}}
{{- $vid := "0" -}}
{{- range $ci := .rawData.draft_return.cart_items -}}
  {{- if eq (toString (int64 $ci.exchange_for_returning_item_id)) (toString (int64 $.inputs.returningItemId)) -}}
    {{- $vid = printf "%d" (int64 $ci.variant_id) -}}
  {{- end -}}
{{- end -}}
{ "ok": true, "state": {{ coalesce .rawData.draft_return.state "ok" | toJson }}, "exchangeVariantId": "{{ $vid }}" }
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