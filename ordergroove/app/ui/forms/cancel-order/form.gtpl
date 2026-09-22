{{- /* Order picker, from the `orders` field added to type Query in 2.3.5. Before
       that field existed no form could offer an order at all, which is why
       skipSubscription shipped with no form. Only actionable orders are listed:
       isActionable is computed in the pull as UNSENT and not locked and not cancelled. */ -}}
{{- $options := list -}}
{{- range $order := .data.orders -}}
{{- if eq (printf "%v" $order.isActionable) "true" -}}
  {{- $label := printf "Order %s" (toString $order.publicId) -}}
  {{- if $order.place -}}{{- $label = printf "%s - due %s" $label (toString $order.place | trunc 10) -}}{{- end -}}
  {{- if $order.total -}}{{- $label = printf "%s - %s" $label (toString $order.total) -}}{{- end -}}
  {{- if $order.statusLabel -}}{{- $label = printf "%s (%s)" $label (toString $order.statusLabel) -}}{{- end -}}
  {{- $options = append $options (dict "text" $label "value" (toString $order.publicId)) -}}
{{- end -}}
{{- end -}}
{
  "title": "Cancel one order",
  {{- if gt (len $options) 0 }}
  "submitButton": "Cancel this order",
  {{- else }}
  "closeButton": "Close",
  "submitButton": "Unavailable",
  {{- end }}
  "sections": [
    {{- if eq (len $options) 0 }}
    {
      "type": "text",
      "text": "This customer has no upcoming Ordergroove orders that can be cancelled."
    }
    {{- else }}
    {
      "type": "input",
      "label": "Order",
      "attr": "orderId",
      "input": {
        "type": "select",
        "placeholder": "Choose the order to cancel",
        "optional": false,
        "options": {{ $options | toJson }}
      },
      "hint": "This cancels ONE order. The subscription keeps running and Ordergroove creates the next order on its normal schedule."
    }
    {{- end }}
  ]
}
