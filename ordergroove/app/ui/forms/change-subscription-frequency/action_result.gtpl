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
  "message": "Delivery frequency changed",
  "detail": {{ printf "Every future order on subscription %s now uses the new cadence." .action.inputs.subscriptionId | toJson }}
}
{{- end -}}
