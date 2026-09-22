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
  "title": "Change delivery frequency",
  {{- if gt (len $options) 0 }}
  "submitButton": "Change frequency",
  {{- else }}
  "closeButton": "Close",
  "submitButton": "Unavailable",
  {{- end }}
  "sections": [
    {{- if eq (len $options) 0 }}
    {
      "type": "text",
      "text": "This customer has no active Ordergroove subscriptions to change."
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
      "label": "Every",
      "attr": "every",
      "input": {
        "type": "text",
        "placeholder": "e.g. 2"
      },
      "hint": "A number. Combined with the period below: 2 + months is every 2 months."
    },
    {
      "type": "input",
      "label": "Period",
      "attr": "everyPeriod",
      "input": {
        "type": "select",
        "placeholder": "Choose a period",
        "optional": false,
        "options": [{"text": "Days", "value": "1"}, {"text": "Weeks", "value": "2"}, {"text": "Months", "value": "3"}, {"text": "Years", "value": "4"}]
      }
    }
    {{- end }}
  ]
}
