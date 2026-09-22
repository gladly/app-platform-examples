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
  "message": "Subscription quantity changed",
  "detail": {{ printf "Subscription %s now ships %s unit(s) on every future order." .action.inputs.subscriptionId (printf "%v" .action.inputs.quantity) | toJson }}
}
{{- end -}}
