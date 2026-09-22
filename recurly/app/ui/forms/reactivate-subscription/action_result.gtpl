{{- if .action.result.error -}}
{
  "errors": [
    {
      "attr": "subscriptionId",
      "detail": {{ printf "Recurly could not reactivate this subscription: %s" (default "please try again or check the subscription in Recurly." .action.result.error.message) | toJson }}
    }
  ]
}
{{- else -}}
{
  "message": "Subscription reactivated",
  "detail": {{ printf "Subscription %s was reactivated and is now active and renewing." .action.inputs.subscriptionId | toJson }}
}
{{- end -}}
