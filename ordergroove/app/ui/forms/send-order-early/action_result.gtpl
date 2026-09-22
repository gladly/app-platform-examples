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
  "message": "Order scheduled to place early",
  "detail": {{ printf "Order %s will be placed within 24 hours. It has not shipped yet - refresh the card to confirm placement." .action.inputs.orderId | toJson }}
}
{{- end -}}
