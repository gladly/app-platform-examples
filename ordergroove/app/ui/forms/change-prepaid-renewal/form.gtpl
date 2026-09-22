{{- /* Build the subscription picker from the `subscriptions` data pull.
       eq (printf "%v" $sub.isPrepaid) "true" is the only thing that differs between forms; the label is shared. */ -}}
{{- $options := list -}}
{{- range $sub := .data.subscriptions -}}
{{- $keep := eq (printf "%v" $sub.isPrepaid) "true" -}}
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
  "title": "Change prepaid renewal",
  {{- if gt (len $options) 0 }}
  "submitButton": "Change renewal",
  {{- else }}
  "closeButton": "Close",
  "submitButton": "Unavailable",
  {{- end }}
  "sections": [
    {{- if eq (len $options) 0 }}
    {
      "type": "text",
      "text": "This customer has no prepaid Ordergroove subscriptions."
    }
    {{- else }}
    {
      "type": "input",
      "label": "Prepaid subscription",
      "attr": "subscriptionId",
      "input": {
        "type": "select",
        "placeholder": "Choose a prepaid subscription",
        "optional": false,
        "options": {{ $options | toJson }}
      }
    },
    {
      "type": "input",
      "label": "At the end of the prepaid term",
      "attr": "renewalBehavior",
      "input": {
        "type": "select",
        "placeholder": "Choose what happens",
        "optional": false,
        "options": [{"text": "Automatically renew", "value": "autorenew"}, {"text": "Cancel the subscription", "value": "cancel"}, {"text": "Downgrade to a standard subscription", "value": "downgrade"}]
      },
      "hint": "Does not change the current term or refund anything already paid."
    }
    {{- end }}
  ]
}
