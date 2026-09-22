{{- if not .action.result -}}
{
  "errors": [
    { "attr": "subscriptionId", "detail": "Ordergroove did not reactivate the subscription. The reason is shown in the conversation timeline." }
  ]
}
{{- else -}}
{
  "message": "Subscription reactivated",
  "detail": {{ printf "Subscription %s is active again, starting %s." .action.inputs.subscriptionId (printf "%v" .action.inputs.startDate) | toJson }}
}
{{- end -}}
