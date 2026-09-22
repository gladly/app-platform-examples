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
  "message": "Prepaid renewal changed",
  "detail": {{ printf "Subscription %s will now %s at the end of its prepaid term." .action.inputs.subscriptionId (printf "%v" .action.inputs.renewalBehavior) | toJson }}
}
{{- end -}}
