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
  "message": "Item quantity changed",
  "detail": {{ printf "That line now shows %s unit(s) on this order only. Future orders are unchanged." (printf "%v" .action.inputs.quantity) | toJson }}
}
{{- end -}}
