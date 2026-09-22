{{- /* skipSubscription has shipped since 2.x with NO form at all, because it needs an
       order id and `orders` was not a field on type Query until 2.3.5. It takes both an
       order and a subscription, so the picker carries both in one value and
       action_inputs.gtpl splits them - the agent cannot pair them wrongly. */ -}}
{{- $options := list -}}
{{- range $order := .data.orders -}}
{{- if and (eq (printf "%v" $order.isActionable) "true") $order.subscriptionId -}}
  {{- $label := printf "Order %s" (toString $order.publicId) -}}
  {{- if $order.place -}}{{- $label = printf "%s - due %s" $label (toString $order.place | trunc 10) -}}{{- end -}}
  {{- if $order.total -}}{{- $label = printf "%s - %s" $label (toString $order.total) -}}{{- end -}}
  {{- $options = append $options (dict "text" $label "value" (printf "%s|%s" (toString $order.publicId) (toString $order.subscriptionId))) -}}
{{- end -}}
{{- end -}}
{
  "title": "Skip the next order",
  {{- if gt (len $options) 0 }}
  "submitButton": "Skip this order",
  {{- else }}
  "closeButton": "Close",
  "submitButton": "Unavailable",
  {{- end }}
  "sections": [
    {{- if eq (len $options) 0 }}
    {
      "type": "text",
      "text": "This customer has no upcoming Ordergroove orders that can be skipped."
    }
    {{- else }}
    {
      "type": "input",
      "label": "Order",
      "attr": "orderRef",
      "input": {
        "type": "select",
        "placeholder": "Choose the order to skip",
        "optional": false,
        "options": {{ $options | toJson }}
      },
      "hint": "Skipping reschedules this subscription's items to the next cycle. The subscription stays active."
    }
    {{- end }}
  ]
}
