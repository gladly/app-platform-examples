{{- if not .action.result -}}
{
  "errors": [
    { "attr": "subscriptionId", "detail": "Ordergroove did not cancel the subscription. The reason is shown in the conversation timeline." }
  ]
}
{{- else -}}
{
  "message": "Subscription cancelled",
  "detail": {{ printf "Subscription %s is cancelled. No further orders will be created." .action.inputs.subscriptionId | toJson }}
}
{{- end -}}
