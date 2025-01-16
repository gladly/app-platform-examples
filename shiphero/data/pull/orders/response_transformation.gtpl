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
[
    {{- range $index, $orderEdge := .rawData.data.orders.data.edges -}}
    {
        {{- $order := $orderEdge.node -}}
        "id": "{{ $order.id }}",

        {{- /* All date-related fields in ShipHero use a UTC timestamp in ISO 8601 format.*/}}
        {{- /* The addition of "Z" is necessary to explicitly indicate that the time is in UTC, without any time zone offset */}}

        "updated_at": "{{ printf `%vZ` $order.updated_at }}",
        "order_date": "{{ printf `%vZ` $order.order_date }}",
        "order_number": "{{ $order.order_number }}",
        "partner_order_id": "{{ $order.partner_order_id }}",
        "fulfillment_status": "{{ $order.fulfillment_status }}",
        "total_tax": "{{ $order.total_tax }}",
        "subtotal": "{{ $order.subtotal }}",
        "total_discounts": "{{ $order.total_discounts }}",
        "total_price": "{{ $order.total_price }}",
        "email": {{ if $order.email }}"{{ $order.email }}"{{ else }}null{{ end }},
        "account_id": "{{ $order.account_id }}",
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
                        "id": "{{ $lineItem.id }}",
                        "product_name": {{ toJson $lineItem.product_name }},
                        "sku": "{{ $lineItem.sku }}",
                        "fulfillment_status": "{{ $lineItem.fulfillment_status }}",
                        "quantity_pending_fulfillment": {{ $lineItem.quantity_pending_fulfillment }},
                        "quantity_shipped": {{ $lineItem.quantity_shipped }},
                        "product": {{ toJson $lineItem.product }}
                    }
                    {{- if lt (add $index 1) (len $order.line_items.edges) -}},{{- end -}}
                {{- end -}}
            {{- end -}}
        ],
        "holds": {{ toJson $order.holds }},
        "currency":{{ if $order.currency }}"{{ $order.currency }}"{{ else }}null{{ end }},
        "hold_until_date": {{ if $order.hold_until_date }}"{{ printf `%vZ` $order.hold_until_date }}"{{ else }}null{{ end }},
        "tax_id": {{ if $order.tax_id }}"{{ $order.tax_id }}"{{ else }}null{{ end }},
        "tax_type": {{ if $order.tax_type }}"{{ $order.tax_type }}"{{ else }}null{{ end }},
        "packing_note": {{ if $order.packing_note }}"{{ $order.packing_note }}"{{ else }}null{{ end }},
        "required_ship_date": "{{ printf `%vZ` $order.required_ship_date }}",
        "flagged": {{ $order.flagged }},
        "saturday_delivery": {{ $order.saturday_delivery }},
        "priority_flag": {{ $order.priority_flag }},
        "third_party_shipper": {{ toJson $order.third_party_shipper}},
        "allow_partial": {{ $order.allow_partial }},
        "require_signature": {{ $order.require_signature }},
        "alcohol": {{ $order.alcohol }},
        "expected_weight_in_oz": {{ $order.expected_weight_in_oz }},
        "has_dry_ice": {{ $order.has_dry_ice }},
        "address_is_business": {{ $order.address_is_business }}
    }
    {{- if lt (add $index 1) (len $.rawData.data.orders.data.edges) -}},{{- end -}}
    {{- end -}}
]
{{- else -}}
[]
{{- end -}}