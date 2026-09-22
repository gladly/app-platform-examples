{{- if not .action.result -}}
{
  "errors": [
    {
      "attr": "orderId",
      "detail": "Ordergroove did not complete the change. The reason is shown in the conversation timeline."
    }
  ]
}
{{- else -}}
{
  "message": "Order cancelled",
  "detail": {{ printf "Order %s was cancelled. The subscription continues - Ordergroove will create the next order on its usual schedule." .action.inputs.orderId | toJson }}
}
{{- end -}}
