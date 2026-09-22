{{- if not .action.result -}}
{
  "errors": [
    {
      "attr": "itemId",
      "detail": "Ordergroove did not complete the change. The reason is shown in the conversation timeline."
    }
  ]
}
{{- else -}}
{
  "message": "Item removed",
  "detail": {{ printf "Item %s was removed from the order. It will return on the next order. Refresh the card to confirm - Ordergroove returns no response body." .action.inputs.itemId | toJson }}
}
{{- end -}}
