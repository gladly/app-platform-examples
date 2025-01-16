{{- if or (eq .inputs.orderId "" ) (eq .inputs.orderId nil) -}}
	{{- fail "Order ID is required."}}
{{- end -}}

{{ $ll := .integration.configuration.lineItemsLimit}}
{{- $lineItemsLimit := $ll | default 10 -}}

{{$query := printf `query {
  shipments(order_id: "%s") {
    request_id
    complexity
    data {
      edges {
        node {
          id
          order_id
          user_id
          warehouse_id
          pending_shipment_id
          picked_up
          needs_refund
          refunded
          delivered
          address {
            name
            address1
            address2
            city
            state
            country
            zip
            phone
          }
          created_date
          line_items(first: %v) {
            edges {
              node {
                id
                shipment_id
                shipping_label_id
                line_item_id
                quantity
              }
            }
          }
          shipping_labels {
            id
            account_id
            shipment_id
            order_id
            box_id
            status
            tracking_number
            order_number
            order_account_id
            carrier
            shipping_name
            shipping_method
            delivered
            picked_up
            refunded
            needs_refund
            partner_fulfillment_id
            full_size_to_print
            packing_slip
            warehouse
            warehouse_id
            insurance_amount
            carrier_account_id
            source
            created_date
            tracking_url
          }
        }
      }
    }
  }
}` .inputs.orderId $lineItemsLimit}}

{
    "query": {{toJson $query}}
}
