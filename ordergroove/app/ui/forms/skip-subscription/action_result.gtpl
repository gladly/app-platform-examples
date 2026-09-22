{{- if not .action.result -}}
{
  "errors": [
    { "attr": "orderRef", "detail": "Ordergroove did not skip the order. The reason is shown in the conversation timeline." }
  ]
}
{{- else -}}
{
  "message": "Order skipped",
  "detail": {{ printf "The items on order %s were rescheduled to the next delivery cycle. The subscription is still active." .action.inputs.orderId | toJson }}
}
{{- end -}}
