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

{{- else if and .rawData.data.shipments.data.edges (gt (len .rawData.data.shipments.data.edges) 0) -}}
[
    {{/* Iterate through all shipments. Simplify edges and nodes out of the response. */}}
    {{- range $index, $shipmentEdge := .rawData.data.shipments.data.edges -}}
    {
        {{- $shipment := $shipmentEdge.node -}}
        "id": "{{ $shipment.id }}",
        "order_id": {{ if $shipment.order_id }}"{{ $shipment.order_id }}"{{ else }}null{{ end }},
        "user_id": {{ if $shipment.user_id }}"{{ $shipment.user_id }}"{{ else }}null{{ end }},
        "warehouse_id": {{ if $shipment.warehouse_id }}"{{ $shipment.warehouse_id }}"{{ else }}null{{ end }},
        "pending_shipment_id": {{ if $shipment.pending_shipment_id }}"{{ $shipment.pending_shipment_id }}"{{ else }}null{{ end }},
        "picked_up": {{ toJson $shipment.picked_up }},
        "needs_refund": {{ toJson $shipment.needs_refund }},
        "refunded": {{ toJson $shipment.refunded }},
        "delivered": {{ toJson $shipment.delivered }},
        "address": {{ toJson $shipment.address }},

        {{- /*  All date-related fields in ShipHero use a UTC timestamp in ISO 8601 format.
                The addition of "Z" is necessary to explicitly indicate that the time is in UTC, without any time zone offset */}}

        "created_date": "{{ printf `%vZ` $shipment.created_date }}",
        "line_items": [
        {{- if not (eq (len $shipment.line_items.edges) 0) -}}
            {{- range $index, $lineItemEdge := $shipment.line_items.edges -}}
                {{- $lineItem := $lineItemEdge.node -}}
                {
                    "id": "{{ $lineItem.id }}",
                    "shipment_id": {{ if $lineItem.shipment_id }}"{{ $lineItem.shipment_id }}"{{ else }}null{{ end }},
                    "shipping_label_id": {{ if $lineItem.shipping_label_id }}"{{ $lineItem.shipping_label_id }}"{{ else }}null{{ end }},
                    "line_item_id": {{ if $lineItem.line_item_id }}"{{ $lineItem.line_item_id }}"{{ else }}null{{ end }},
                    "quantity": {{ $lineItem.quantity }}
                }{{- if lt (add $index 1) (len $shipment.line_items.edges) -}},{{- end -}}
            {{- end -}}
        {{- end -}}
        ],
        "shipping_labels": [
        {{- if not (eq (len $shipment.shipping_labels) 0) -}}
            {{- range $index, $shippingLabel := $shipment.shipping_labels -}}
                {
                    "id": "{{ $shippingLabel.id }}",
                    "account_id": "{{ $shippingLabel.account_id }}",
                    "shipment_id": "{{ $shippingLabel.shipment_id }}",
                    "order_id": "{{ $shippingLabel.order_id }}",
                    "box_id": {{ toJson $shippingLabel.box_id }},
                    "status": "{{ $shippingLabel.status }}",
                    "tracking_number": "{{ $shippingLabel.tracking_number }}",
                    "order_number": "{{ $shippingLabel.order_number }}",
                    "order_account_id": "{{ $shippingLabel.order_account_id }}",
                    "carrier": "{{ $shippingLabel.carrier }}",
                    "shipping_name": {{ toJson $shippingLabel.shipping_name }},
                    "shipping_method": "{{ $shippingLabel.shipping_method }}",
                    "delivered": {{ toJson $shippingLabel.delivered }},
                    "picked_up": {{ toJson $shippingLabel.picked_up }},
                    "refunded": {{ toJson $shippingLabel.refunded }},
                    "needs_refund": {{ toJson $shippingLabel.needs_refund }},
                    "partner_fulfillment_id": {{ toJson $shippingLabel.partner_fulfillment_id }},
                    "full_size_to_print": {{ toJson $shippingLabel.full_size_to_print }},
                    "packing_slip": {{ if $shippingLabel.packing_slip }}{{ toJson $shippingLabel.packing_slip }}{{ else }}null{{ end }},
                    "warehouse": "{{ $shippingLabel.warehouse }}",
                    "warehouse_id": {{ toJson $shippingLabel.warehouse_id }},
                    "insurance_amount": "{{ $shippingLabel.insurance_amount }}",
                    "carrier_account_id": {{ toJson $shippingLabel.carrier_account_id }},
                    "source": {{ toJson $shippingLabel.source }},
                    "created_date": "{{ printf `%vZ` $shippingLabel.created_date }}",
                    "tracking_url": "{{ $shippingLabel.tracking_url }}"
                }{{- if lt (add $index 1) (len $shipment.shipping_labels) -}},{{- end -}}
            {{- end -}}
        {{- end -}}
        ]
    }{{- if lt (add $index 1) (len $.rawData.data.shipments.data.edges) -}},{{- end -}}
    {{- end -}}
]
{{- else -}}
[]
{{- end -}}
