{{- $errors := .rawData.errors -}}

{{- if or (ne .response.statusCode 200) $errors -}}
    {{- $errorMessages := "" -}}
    {{- $requestId := "" -}}

    {{- if gt (len $errors) 0 -}}
        {{- $requestId = (index $errors 0).request_id -}} 
        {{- range $index, $error := $errors -}}
            {{- $field := $error.message -}}
            {{- if $index -}}
                {{- $errorMessages = (print $errorMessages "; " $field " - " $error.code) -}}
            {{- else -}}
                {{- $errorMessages = (print $field " - " $error.code) -}}
            {{- end -}}
        {{- end -}}
    {{- end -}}

   {{- fail (printf "Request ID: %s; Errors: %s" (or $requestId "unknown") $errorMessages) -}}

{{- else if and .rawData.data.orders.data.edges (gt (len .rawData.data.orders.data.edges) 0) (ne .rawData nil) -}}

    { 
        {{- $order := (index .rawData.data.orders.data.edges 0).node -}}
        "id": "{{ $order.id }}",
        "order_number": "{{ $order.order_number }}",
        "partner_order_id": {{ if $order.partner_order_id }}"{{ $order.partner_order_id }}"{{ else }}""{{ end }},
        "fulfillment_status": {{ toJson $order.fulfillment_status }},
        "total_tax": "{{ $order.total_tax }}",
        "subtotal": "{{ $order.subtotal }}",
        "total_discounts": "{{ $order.total_discounts }}",
        "total_price": "{{ $order.total_price }}",

        {{- /*  All date-related fields in ShipHero use a UTC timestamp in ISO 8601 format.
                The addition of "Z" is necessary to explicitly indicate that the time is in UTC, without any time zone offset */}}

        "order_date": "{{ printf `%vZ` $order.order_date }}",
        "updated_at": "{{ printf `%vZ` $order.updated_at }}",
        "email": {{ toJson $order.email }},
        "account_id": {{ if $order.account_id }}"{{ $order.account_id }}"{{ else }}""{{ end }},
        "shipping_lines": {{ toJson $order.shipping_lines }},
        "shipping_address": {{ toJson $order.shipping_address }},
        "billing_address": {{ toJson $order.billing_address }},
        "tags": {{ toJson $order.tags }},
        "line_items": [
            {{- if not (eq (len $order.line_items.edges) 0) -}}
                {{- range $index, $lineItemEdge := $order.line_items.edges -}}
                    {{- $lineItem := $lineItemEdge.node -}}
                    {
                        "quantity": {{ $lineItem.quantity }},
                        "id": {{ if $lineItem.id }}"{{ $lineItem.id }}"{{ else }}""{{ end }},
                        "product_name": {{ toJson $lineItem.product_name }},
                        "sku": {{ toJson $lineItem.sku }},
                        "fulfillment_status": {{ toJson $lineItem.fulfillment_status }},
                        "product": {{ toJson $lineItem.product }}
                    }
                    {{- if lt (add $index 1) (len $order.line_items.edges) -}},{{- end -}}
                {{- end -}}
            {{- end -}}
        ],
        "holds": {{ toJson $order.holds }},
        "currency": {{ toJson $order.currency }},
        "hold_until_date": {{ toJson $order.hold_until_date }},
        "tax_id": {{ if $order.tax_id }}"{{ $order.tax_id }}"{{ else }}""{{ end }},
        "tax_type": {{ toJson $order.tax_type }}
    }
{{- else -}}
null
{{- end -}}
