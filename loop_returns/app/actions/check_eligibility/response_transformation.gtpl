{{- /* Handle non-success status codes with a clear, agent-readable message */ -}}
{{ if and (ge .response.statusCode 200) (lt .response.statusCode 300) }}
{
    "draftReturnId": "{{ int64 .rawData.draft_return.id }}",
    "state": {{ .rawData.draft_return.state | toJson }},
    "itemsEligible": [
        {{- range $i, $e := .rawData.context.items_eligible_to_return }}{{if $i}},{{end}}
        { "orderLineItemId": "{{ int64 $e.order_line_item_id }}" }
        {{- end }}
    ],
    "itemsNotEligible": [
        {{- range $i, $e := .rawData.context.items_not_eligible_to_return }}{{if $i}},{{end}}
        { "orderLineItemId": "{{ int64 $e.order_line_item_id }}", "ineligibilityCode": {{ $e.ineligibility_code | toJson }} }
        {{- end }}
    ],
    "lineItems": [
        {{- range $i, $li := .rawData.context.order.line_items }}{{if $i}},{{end}}
        { "id": "{{ int64 $li.id }}", "title": {{ $li.title | toJson }}, "variantTitle": {{ $li.variant_title | toJson }}, "price": {{ $li.price | default 0 }}, "image": {{ $li.image | toJson }}, "providerProductId": "{{ int64 $li.provider_product_id }}", "providerVariantId": "{{ int64 $li.provider_variant_id }}" }
        {{- end }}
    ],
    "returnMethodOptions": [
        {{- range $i, $m := .rawData.return_method_options }}{{if $i}},{{end}}
        { "id": "{{ int64 $m.id }}", "name": {{ $m.name | toJson }}, "type": {{ $m.type | toJson }} }
        {{- end }}
    ]
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