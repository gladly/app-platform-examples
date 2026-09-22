{{- /* Build a dropdown of only paused subscriptions — resume requires the paused state. */ -}}
{{- $options := list -}}
{{- range $sub := .data.account.subscriptions -}}
  {{- $state := lower (toString $sub.state) -}}
  {{- if eq $state "paused" -}}
    {{- $planName := "Subscription" -}}
    {{- if $sub.plan -}}{{- $planName = $sub.plan.name -}}{{- end -}}
    {{- $label := printf "%s — %s" $planName $sub.id -}}
    {{- $options = append $options (dict "text" $label "value" $sub.id) -}}
  {{- end -}}
{{- end -}}
{
  "title": "Resume Subscription",
  {{- if gt (len $options) 0 }}
  "submitButton": "Resume Subscription",
  {{- else }}
  "closeButton": "Close",
  "submitButton": "Unavailable",
  {{- end }}
  "sections": [
    {{- if eq (len $options) 0 }}
    {
      "type": "text",
      "text": "This customer has no paused subscriptions to resume."
    }
    {{- else }}
    {
      "type": "input",
      "label": "Subscription",
      "attr": "subscriptionId",
      "input": {
        "type": "select",
        "placeholder": "Choose a subscription to resume",
        "optional": false,
        "options": {{ $options | toJson }}
      },
      "hint": "Resuming immediately returns the subscription to an active, renewing state."
    }
    {{- end }}
  ]
}
