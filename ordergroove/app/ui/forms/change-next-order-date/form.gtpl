{{- /* Build the subscription picker from the `subscriptions` data pull.
       eq (printf "%v" $sub.statusLabel) "Active" is the only thing that differs between forms; the label is shared. */ -}}
{{- $options := list -}}
{{- range $sub := .data.subscriptions -}}
{{- $keep := eq (printf "%v" $sub.statusLabel) "Active" -}}
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
  "title": "Move the next order date",
  {{- if gt (len $options) 0 }}
  "submitButton": "Move next order",
  {{- else }}
  "closeButton": "Close",
  "submitButton": "Unavailable",
  {{- end }}
  "sections": [
    {{- if eq (len $options) 0 }}
    {
      "type": "text",
      "text": "This customer has no active Ordergroove subscriptions to reschedule."
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
      }
    },
    {
      "type": "input",
      "label": "New date",
      "attr": "orderDate",
      "input": {
        "type": "text",
        "placeholder": "YYYY-MM-DD"
      },
      "hint": "Must be a future date, in YYYY-MM-DD format."
    }
    {{- end }}
  ]
}
