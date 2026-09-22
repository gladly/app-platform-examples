{{- if .action.result.error -}}
{
  "errors": [
    {
      "attr": "subscriptionId",
      "detail": {{ printf "Recurly could not resume this subscription: %s" (default "please try again or check the subscription in Recurly." .action.result.error.message) | toJson }}
    }
  ]
}
{{- else -}}
{
  "message": "Subscription resumed",
  "detail": {{ printf "Subscription %s was resumed and is now active and renewing." .action.inputs.subscriptionId | toJson }}
}
{{- end -}}
