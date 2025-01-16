{{- if or (eq .inputs.orderNum "" ) (eq .inputs.orderNum nil) -}}
    {{- fail "Order Number is required." -}}
{{- end -}}

{{ $ll := .integration.configuration.lineItemsLimit}}
{{- $lineItemsLimit := $ll | default 40 -}}

{{- /* Define the GraphQL query */}}
{{- $query := printf `query {
  orders(order_number: "%s") {
    request_id
    complexity
    data(first: 1) {
      edges {
        node {
          id
          order_number
          partner_order_id
          fulfillment_status
          total_tax
          subtotal
          total_discounts
          total_price
          order_date
          updated_at
          email
          account_id
          shipping_lines {
            title
            carrier
            method
            price
          }
          shipping_address {
            phone
            email
            address1
            address2
            city
            state
            country
            zip
          }
          billing_address {
            phone
            email
            address1
            address2
            city
            state
            country
            zip
          }
          tags
          line_items(first: %v) {
            edges {
              node {
                quantity
                id
                product_name
                sku
                fulfillment_status
                product {
                  id
                  name
                  tags
                }
              }
            }
          }
          holds {
            fraud_hold
            address_hold
            shipping_method_hold
            operator_hold
            payment_hold
            client_hold
          }
          currency
          hold_until_date
          tax_id
          tax_type
        }
      }
    }
  }
}` .inputs.orderNum $lineItemsLimit -}}

{
    "query": {{- toJson $query -}}
}
