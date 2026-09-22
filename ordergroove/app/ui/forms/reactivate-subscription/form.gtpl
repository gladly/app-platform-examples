{{- $options := list -}}
{{- range $sub := .data.subscriptions -}}
{{- if ne (printf "%v" $sub.statusLabel) "Active" -}}
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
  "title": "Reactivate a subscription",
  {{- if gt (len $options) 0 }}
  "submitButton": "Reactivate",
  {{- else }}
  "closeButton": "Close",
  "submitButton": "Unavailable",
  {{- end }}
  "sections": [
    {{- if eq (len $options) 0 }}
    {
      "type": "text",
      "text": "This customer has no cancelled or inactive Ordergroove subscriptions to reactivate."
    }
    {{- else }}
    {
      "type": "input",
      "label": "Subscription",
      "attr": "subscriptionId",
      "input": {
        "type": "select",
        "placeholder": "Choose a subscription to reactivate",
        "optional": false,
        "options": {{ $options | toJson }}
      }
    },
    {
      "type": "input",
      "label": "Every",
      "attr": "every",
      "input": { "type": "text", "placeholder": "e.g. 2" },
      "hint": "Reactivating sets the cadence at the same time. 2 + Months is every 2 months."
    },
    {
      "type": "input",
      "label": "Period",
      "attr": "everyPeriod",
      "input": {
        "type": "select",
        "placeholder": "Choose a period",
        "optional": false,
        "options": [{"text":"Days","value":"1"},{"text":"Weeks","value":"2"},{"text":"Months","value":"3"},{"text":"Years","value":"4"}]
      }
    },
    {
      "type": "input",
      "label": "Start date",
      "attr": "startDate",
      "input": { "type": "text", "placeholder": "YYYY-MM-DD" },
      "hint": "When the reactivated subscription should next order."
    }
    {{- end }}
  ]
}
