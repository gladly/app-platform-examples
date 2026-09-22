{{- /* Build a dropdown of only canceled subscriptions — reactivate applies to canceled subs.
       Expired or failed subscriptions cannot be reactivated. */ -}}
{{- $options := list -}}
{{- range $sub := .data.account.subscriptions -}}
  {{- $state := lower (toString $sub.state) -}}
  {{- if eq $state "canceled" -}}
    {{- $planName := "Subscription" -}}
    {{- if $sub.plan -}}{{- $planName = $sub.plan.name -}}{{- end -}}
    {{- $label := printf "%s — %s" $planName $sub.id -}}
    {{- $options = append $options (dict "text" $label "value" $sub.id) -}}
  {{- end -}}
{{- end -}}
{
  "title": "Reactivate Subscription",
  {{- if gt (len $options) 0 }}
  "submitButton": "Reactivate Subscription",
  {{- else }}
  "closeButton": "Close",
  "submitButton": "Unavailable",
  {{- end }}
  "sections": [
    {{- if eq (len $options) 0 }}
    {
      "type": "text",
      "text": "This customer has no canceled subscriptions to reactivate. Expired or failed subscriptions cannot be reactivated."
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
      },
      "hint": "Reactivation returns the subscription to an active, renewing state on its original billing cycle."
    }
    {{- end }}
  ]
}
