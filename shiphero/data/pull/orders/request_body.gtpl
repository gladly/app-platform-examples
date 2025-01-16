{{- /* Don't process the template if customer profile has no email addresses. */}}
{{- if eq (len .customer.emailAddresses) 0}}
	{{- stop "unable to retrieve customer data since the customer profile does not have any email addresses"}}
{{- end}}

{{- $email := ""}}
{{- if .customer.primaryEmailAddress}}
	{{- /* Check if primary email address is set */}}
	{{- $email = .customer.primaryEmailAddress}}
{{- else if gt (len .customer.emailAddresses) 0 }}
	{{- /* Check if there's an email in the emailAddresses array. */}}
	{{- $email = index .customer.emailAddresses 0 }}
{{- end}}

{{ $ol := .integration.configuration.ordersLimit}}
{{- $ordersLimit := $ol | default 40 -}}

{{ $ll := .integration.configuration.lineItemsLimit}}
{{- $lineItemsLimit := $ll | default 40 -}}


{{$query := printf `query {
  orders(email: "%s") {
    request_id
    complexity
    data(first: %v, sort: "-order_date") {
      edges {
        node {
          id
          updated_at
          order_date
          order_number
          partner_order_id
          fulfillment_status
          total_tax
          subtotal
          total_discounts
          total_price
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
                quantity_pending_fulfillment,
                quantity_shipped,
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
          packing_note
          required_ship_date
          flagged
          saturday_delivery
          priority_flag
          third_party_shipper { 
            account_number
            zip
            country
          }
          allow_partial
          require_signature
          alcohol
          expected_weight_in_oz
          has_dry_ice
          address_is_business
        }
      }
    }
  }
}` $email $ordersLimit $lineItemsLimit }}

{
  "query": {{toJson $query}}
}
