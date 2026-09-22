{{- $options := list -}}
{{- range $sub := .data.subscriptions -}}
{{- if eq (printf "%v" $sub.statusLabel) "Active" -}}
  {{- $name := printf "Subscription %s" (toString $sub.publicId) -}}
  {{- if $sub.productDetail -}}
    {{- if $sub.productDetail.name -}}{{- $name = toString $sub.productDetail.name -}}{{- end -}}
  {{- end -}}
  {{- $label := $name -}}
  {{- if $sub.cadenceLabel -}}{{- $label = printf "%s - %s" $label (toString $sub.cadenceLabel) -}}{{- end -}}
  {{- if $sub.statusLabel -}}{{- $label = printf "%s (%s)" $label (toString $sub.statusLabel) -}}{{- end -}}
  {{- $options = append $options (dict "text" $label "value" (toString $sub.publicId)) -}}
{{- end -}}
{{- end -}}
{
  "title": "Cancel a subscription",
  {{- if gt (len $options) 0 }}
  "submitButton": "Cancel subscription",
  {{- else }}
  "closeButton": "Close",
  "submitButton": "Unavailable",
  {{- end }}
  "sections": [
    {{- if eq (len $options) 0 }}
    {
      "type": "text",
      "text": "This customer has no active Ordergroove subscriptions to cancel."
    }
    {{- else }}
    {
      "type": "text",
      "text": "Before cancelling, consider a save lever: change the frequency, reduce the quantity, move the next order, or issue a one-time discount."
    },
    {
      "type": "input",
      "label": "Subscription",
      "attr": "subscriptionId",
      "input": {
        "type": "select",
        "placeholder": "Choose a subscription to cancel",
        "optional": false,
        "options": {{ $options | toJson }}
      }
    },
    {
      "type": "input",
      "label": "Reason code",
      "attr": "cancelReasonCode",
      "input": {
        "type": "text",
        "optional": true,
        "placeholder": "e.g. 1"
      },
      "hint": "Your store's cancellation reason codes. Both this and the detail below must be filled in for Ordergroove to record a reason."
    },
    {
      "type": "input",
      "label": "Reason detail",
      "attr": "cancelReasonDetails",
      "input": {
        "type": "text",
        "optional": true,
        "placeholder": "What the customer told you"
      }
    }
    {{- end }}
  ]
}
