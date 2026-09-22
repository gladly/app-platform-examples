{{- /* Handle non-success status codes with a clear, agent-readable message */ -}}
{{ if and (ge .response.statusCode 200) (lt .response.statusCode 300) }}
{
    "draftReturnId": "{{ int64 .rawData.draft_return.id }}",
    "state": {{ .rawData.draft_return.state | toJson }},
    "returningItems": [
        {{- range $i, $ri := .rawData.draft_return.returning_items }}{{if $i}},{{end}}
        { "id": "{{ int64 $ri.id }}", "orderLineItemId": "{{ int64 $ri.order_line_item_id }}", "returnType": {{ $ri.return_type | toJson }} }
        {{- end }}
    ],
    "links": [
        {{- range $i, $l := .rawData.links }}{{if $i}},{{end}}
        { "rel": {{ $l.rel | toJson }}, "href": {{ $l.href | toJson }}, "method": {{ $l.method | toJson }}, "isRequired": {{ $l.is_required | default false }} }
        {{- end }}
    ]
    ,"exchangeOptions": [
        {{- $first := true -}}
        {{- range $itemId, $eo := .rawData.context.exchange_options }}
            {{- if not $first }},{{ end }}{{- $first = false -}}
            {
                "returningItemId": "{{ $itemId }}",
                "productOptions": [
                    {{- range $j, $po := $eo.product_options }}{{if $j}},{{end}}
                    { "name": {{ $po.name | toJson }}, "values": {{ $po.values | toJson }} }
                    {{- end }}
                ],
                "variants": [
                    {{- range $k, $v := $eo.product_variants }}{{if $k}},{{end}}
                    {
                        "productId": "{{ int64 $v.product_id }}",
                        "variantId": "{{ int64 $v.variant_id }}",
                        "title": {{ $v.title | toJson }},
                        "variantTitle": {{ $v.variant_title | toJson }},
                        "productType": {{ $v.product_type | toJson }},
                        "options": [
                            {{- range $oi, $on := (keys $v.options | sortAlpha) }}{{if $oi}},{{end}}
                            { "name": {{ $on | toJson }}, "value": {{ index $v.options $on | toJson }} }
                            {{- end }}
                        ],
                        "image": {{ $v.image | toJson }},
                        "price": {{ $v.price | toJson }},
                        "currency": {{ $v.currency | toJson }},
                        "isAvailable": {{ $v.is_available | toJson }}
                    }
                    {{- end }}
                ]
            }
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