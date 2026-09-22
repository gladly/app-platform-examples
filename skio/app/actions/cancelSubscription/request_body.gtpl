{{- $input := dict "subscriptionId" (.inputs.subscriptionId | toString) -}}
{{- if ne .inputs.shouldSendNotif nil -}}{{- $input = set $input "shouldSendNotif" (.inputs.shouldSendNotif) -}}{{- end -}}
{{- if ne .inputs.permanentlyCancel nil -}}{{- $input = set $input "permanentlyCancel" (.inputs.permanentlyCancel) -}}{{- end -}}
{{- if ne .inputs.invokeSubscriptionCancelJourneys nil -}}{{- $input = set $input "invokeSubscriptionCancelJourneys" (.inputs.invokeSubscriptionCancelJourneys) -}}{{- end -}}
{{- if and (ne .inputs.cancelSessionId nil) (ne (.inputs.cancelSessionId | toString | trim) "") -}}{{- $input = set $input "cancelSessionId" (.inputs.cancelSessionId | toString) -}}{{- end -}}
{
  "query": "mutation cancelSubscription($input: CancelSubscriptionInput!) { cancelSubscription(input: $input) { ok } }",
  "variables": {
    "input": {{ toJson $input }}
  }
}
