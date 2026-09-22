{{- if .action.result.error -}}
{
  "errors": [
    {
      "attr": "subscriptionId",
      "detail": {{ printf "Recurly could not cancel this subscription: %s" (default "please try again or check the subscription in Recurly." .action.result.error.message) | toJson }}
    }
  ]
}
{{- else -}}
{
  "message": "Subscription canceled",
  "detail": {{ printf "Subscription %s was canceled and will expire at the end of the current billing cycle. It can be reactivated until then." .action.inputs.subscriptionId | toJson }}
}
{{- end -}}
