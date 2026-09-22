{{- /* Handle non-success status codes with a clear, agent-readable message */ -}}
{{ if and (ge .response.statusCode 200) (lt .response.statusCode 300) -}}
{{- /* Loop returns the updated draft return, not the single created item. Find the
       returning item whose order_line_item_id matches the one we just added and
       return its id (compared as normalized integer strings). */ -}}
{{- $items := coalesce (dig "draft_return" "returning_items" (list) .rawData) (dig "returning_items" (list) .rawData) -}}
{{- $target := toString (int64 .inputs.orderLineItemId) -}}
{{- $match := dict -}}
{{- range $ri := $items -}}
    {{- if eq (toString (int64 $ri.order_line_item_id)) $target -}}{{- $match = $ri -}}{{- end -}}
{{- end -}}
{{- if not $match.id -}}
    {{ printf "the item was added but Loop's response contained no returning item for order line item %s" $target | stop }}
{{- else -}}
{ "returningItemId": "{{ int64 $match.id }}", "orderLineItemId": "{{ int64 $match.order_line_item_id }}" }
{{- end }}
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
