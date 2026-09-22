{{- /* This form is an affordance, NOT a control. Every gate that matters - the merchant
       toggle, the configured cap, fail-closed-when-unset, and the typed approval - is
       enforced in the ACTION template, because Team Assist invokes the action directly
       and never renders this form. What is here only shapes what a human sees. */ -}}
{{- $customerId := "" -}}
{{- $merchantId := "" -}}
{{- range $c := .data.customers -}}
  {{- if eq $customerId "" -}}
    {{- $customerId = toString $c.id -}}
    {{- if $c.merchant -}}{{- $merchantId = toString $c.merchant -}}{{- end -}}
  {{- end -}}
{{- end -}}
{{- /* The customer record does not always carry `merchant`; every subscription and order
       does, so fall back to one of those rather than asking the agent to type it. */ -}}
{{- if eq $merchantId "" -}}
  {{- range $o := .data.orders -}}
    {{- if and (eq $merchantId "") $o.merchant -}}{{- $merchantId = toString $o.merchant -}}{{- end -}}
  {{- end -}}
{{- end -}}
{{- /* The items pull deliberately fetches line items for up to ten already-completed
       (SUCCESS/CANCELLED/MERGED) orders so the profile card can show order history. A
       discount on a completed order creates an incentive that can never apply, so item
       targets are restricted to items whose parent order is still actionable. An Item
       carries `order` but no order status, so the actionable set is built from the
       isActionable boolean the orders pull computes. Whole-order targets below are
       filtered on the same flag. */ -}}
{{- $actionableOrders := dict -}}
{{- range $order := .data.orders -}}
  {{- if eq (printf "%v" $order.isActionable) "true" -}}
    {{- $_ := set $actionableOrders (toString $order.publicId) true -}}
  {{- end -}}
{{- end -}}
{{- $targets := list -}}
{{- range $item := .data.items -}}
  {{- if hasKey $actionableOrders (toString $item.order) -}}
  {{- $name := "" -}}
  {{- if $item.productDetail -}}
    {{- if $item.productDetail.name -}}{{- $name = toString $item.productDetail.name -}}{{- end -}}
  {{- end -}}
  {{- if eq $name "" -}}{{- $name = printf "Product %s" (toString $item.product) -}}{{- end -}}
  {{- $label := printf "Item: %s" $name -}}
  {{- if $item.totalPrice -}}{{- $label = printf "%s - %s" $label (toString $item.totalPrice) -}}{{- end -}}
  {{- $targets = append $targets (dict "text" $label "value" (printf "item|%s|%s|%s" (toString $item.publicId) $customerId $merchantId)) -}}
  {{- end -}}
{{- end -}}
{{- range $order := .data.orders -}}
  {{- if eq (printf "%v" $order.isActionable) "true" -}}
    {{- $label := printf "Whole order %s" (toString $order.publicId) -}}
    {{- if $order.total -}}{{- $label = printf "%s - %s" $label (toString $order.total) -}}{{- end -}}
    {{- $targets = append $targets (dict "text" $label "value" (printf "order|%s|%s|%s" (toString $order.publicId) $customerId $merchantId)) -}}
  {{- end -}}
{{- end -}}
{
  "title": "Issue a one-time discount",
  {{- if or (eq $customerId "") (eq (len $targets) 0) }}
  "closeButton": "Close",
  "submitButton": "Unavailable",
  {{- else }}
  "submitButton": "Issue discount",
  {{- end }}
  "sections": [
    {{- if eq $customerId "" }}
    {
      "type": "text",
      "text": "No Ordergroove customer record is loaded, so a discount cannot be issued."
    }
    {{- else if eq (len $targets) 0 }}
    {
      "type": "text",
      "text": "This customer has no items or upcoming orders a discount could be applied to."
    }
    {{- else }}
    {
      "type": "text",
      "text": "Discounts apply when the order places. Ordergroove has no idempotency key, so submitting twice issues TWO discounts."
    },
    {
      "type": "input",
      "label": "Apply to",
      "attr": "target",
      "input": {
        "type": "select",
        "placeholder": "Choose an item or a whole order",
        "optional": false,
        "options": {{ $targets | toJson }}
      }
    },
    {
      "type": "input",
      "label": "Discount type",
      "attr": "discountType",
      "input": {
        "type": "select",
        "placeholder": "Choose a type",
        "optional": false,
        "options": [{"text": "Fixed amount off", "value": "amount"}, {"text": "Percentage off", "value": "percent"}]
      }
    },
    {
      "type": "input",
      "label": "Value",
      "attr": "value",
      "input": {
        "type": "text",
        "placeholder": "e.g. 5"
      },
      "hint": "5 with Fixed amount is 5 off. 10 with Percentage is 10% off. Your store's configured maximum still applies."
    },
    {
      "type": "input",
      "label": "Reason",
      "attr": "reason",
      "input": {
        "type": "text",
        "placeholder": "e.g. Late delivery goodwill"
      },
      "hint": "Recorded on the incentive in Ordergroove for later reporting."
    },
    {
      "type": "input",
      "label": "Confirmation",
      "attr": "confirmed",
      "input": { "type": "checkbox", "text": "I confirm this one-time discount" },
      "hint": "This issues money against the customer's order. The store's configured maximum still applies."
    }
    {{- end }}
  ]
}
