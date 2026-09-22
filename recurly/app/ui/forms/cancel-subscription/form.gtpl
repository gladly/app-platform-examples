{{- /* Build a dropdown of only the subscriptions that can be canceled: active or paused. */ -}}
{{- $options := list -}}
{{- range $sub := .data.account.subscriptions -}}
  {{- $state := lower (toString $sub.state) -}}
  {{- if or (eq $state "active") (eq $state "paused") -}}
    {{- $planName := "Subscription" -}}
    {{- if $sub.plan -}}{{- $planName = $sub.plan.name -}}{{- end -}}
    {{- $label := printf "%s (%s) — %s" $planName (title $state) $sub.id -}}
    {{- $options = append $options (dict "text" $label "value" $sub.id) -}}
  {{- end -}}
{{- end -}}
{
  "title": "Cancel Subscription",
  {{- if gt (len $options) 0 }}
  "submitButton": "Cancel Subscription",
  {{- else }}
  "closeButton": "Close",
  "submitButton": "Unavailable",
  {{- end }}
  "sections": [
    {{- if eq (len $options) 0 }}
    {
      "type": "text",
      "text": "This customer has no active or paused subscriptions to cancel."
    }
    {{- else }}
    {
      "type": "input",
      "label": "Subscription",
      "attr": "subscriptionId",
      "input": {
        "type": "select",
        "placeholder": "Choose a subscription to cancel",
        "optional": false,
        "options": {{ $options | toJson }}
      },
      "hint": "The subscription stays active until the end of the current billing cycle, then expires. It can be reactivated until then."
    }
    {{- end }}
  ]
}
