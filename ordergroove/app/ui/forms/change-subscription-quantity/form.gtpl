{{- /* Build the subscription picker from the `subscriptions` data pull.
       and (eq (printf "%v" $sub.statusLabel) "Active") (ne (printf "%v" $sub.isPrepaid) "true") is the only thing that differs between forms; the label is shared. */ -}}
{{- $options := list -}}
{{- range $sub := .data.subscriptions -}}
{{- $keep := and (eq (printf "%v" $sub.statusLabel) "Active") (ne (printf "%v" $sub.isPrepaid) "true") -}}
{{- if $keep -}}
  {{- $name := printf "Subscription %s" (toString $sub.publicId) -}}
  {{- if $sub.productDetail -}}
    {{- if $sub.productDetail.name -}}{{- $name = toString $sub.productDetail.name -}}{{- end -}}
  {{- end -}}
  {{- $label := $name -}}
  {{- if $sub.cadenceLabel -}}{{- $label = printf "%s - %s" $label (toString $sub.cadenceLabel) -}}{{- end -}}
  {{- if $sub.quantityLabel -}}{{- $label = printf "%s - qty %s" $label (toString $sub.quantityLabel) -}}{{- end -}}
  {{- if $sub.statusLabel -}}{{- $label = printf "%s (%s)" $label (toString $sub.statusLabel) -}}{{- end -}}
  {{- $options = append $options (dict "text" $label "value" (toString $sub.publicId)) -}}
{{- end -}}
{{- end -}}
{
  "title": "Change subscription quantity",
  {{- if gt (len $options) 0 }}
  "submitButton": "Change quantity",
  {{- else }}
  "closeButton": "Close",
  "submitButton": "Unavailable",
  {{- end }}
  "sections": [
    {{- if eq (len $options) 0 }}
    {
      "type": "text",
      "text": "This customer has no active non-prepaid Ordergroove subscriptions. Quantity cannot be changed on a prepaid subscription."
    }
    {{- else }}
    {
      "type": "input",
      "label": "Subscription",
      "attr": "subscriptionId",
      "input": {
        "type": "select",
        "placeholder": "Choose a subscription",
        "optional": false,
        "options": {{ $options | toJson }}
      },
      "hint": "Prepaid subscriptions are excluded - Ordergroove rejects quantity changes on them."
    },
    {
      "type": "input",
      "label": "New quantity",
      "attr": "quantity",
      "input": {
        "type": "text",
        "placeholder": "e.g. 2"
      },
      "hint": "Applies to every future order. To change just one order, use Change item quantity."
    }
    {{- end }}
  ]
}
