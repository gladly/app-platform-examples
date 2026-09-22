{{- /* A stop in the action template surfaces as an absent result. Guard before
       dereferencing - and compare through printf "%v", since eq against a
       possibly-absent value errors at render time. */ -}}
{{- if not .action.result -}}
{
  "errors": [
    {
      "attr": "subscriptionId",
      "detail": "Ordergroove did not complete the change. The reason is shown in the conversation timeline."
    }
  ]
}
{{- else -}}
{
  "message": "Next order date requested",
  "detail": {{ printf "The next order on subscription %s was moved to %s. Refresh the card to confirm - Ordergroove does not return the new date." .action.inputs.subscriptionId (printf "%v" .action.inputs.orderDate) | toJson }}
}
{{- end -}}
