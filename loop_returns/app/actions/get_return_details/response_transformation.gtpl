{{- /* Handle non-success status codes with a clear, agent-readable message */ -}}
{{ if and (ge .response.statusCode 200) (lt .response.statusCode 300) }}
{{- if not .rawData.id -}}
    {{ printf "no return found: %s" (coalesce .rawData.message "unknown") | stop }}
{{- else -}}
{
    "id": "{{ int64 .rawData.id }}",
    "state": {{ .rawData.state | toJson }},
    "orderName": {{ .rawData.order_name | toJson }},
    "orderNumber": {{ .rawData.order_number | toJson }},
    "customerEmail": {{ .rawData.customer_email | toJson }},
    "currency": {{ .rawData.currency | toJson }},
    "returnTotal": {{ if hasKey .rawData "return_total" }}{{ .rawData.return_total }}{{ else }}0{{ end }},
    "refund": {{ if hasKey .rawData "refund" }}{{ .rawData.refund }}{{ else }}0{{ end }},
    "exchange": {{ if hasKey .rawData "exchange" }}{{ .rawData.exchange }}{{ else }}0{{ end }},
    "giftCard": {{ if hasKey .rawData "gift_card" }}{{ .rawData.gift_card }}{{ else }}0{{ end }},
    "handlingFee": {{ if hasKey .rawData "handling_fee" }}{{ .rawData.handling_fee }}{{ else }}0{{ end }},
    "carrier": {{ .rawData.carrier | toJson }},
    "labelStatus": {{ .rawData.label_status | toJson }},
    "labelUrl": {{ .rawData.label_url | toJson }},
    "trackingNumber": {{ .rawData.tracking_number | toJson }},
    "statusPageUrl": {{ .rawData.status_page_url | toJson }},
    "type": {{ .rawData.type | toJson }},
    "lineItems": [
        {{- range $i, $li := .rawData.line_items }}{{if $i}},{{end}}
        { "id": "{{ int64 $li.id }}", "title": {{ $li.title | toJson }}, "sku": {{ $li.sku | toJson }}, "quantity": {{ if hasKey $li "quantity" }}{{ $li.quantity }}{{ else }}0{{ end }}, "refundAmount": {{ if hasKey $li "refund" }}{{ $li.refund }}{{ else }}0{{ end }}, "condition": {{ $li.condition | toJson }}, "disposition": {{ $li.disposition | toJson }} }
        {{- end }}
    ]
}
{{- end -}}
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
