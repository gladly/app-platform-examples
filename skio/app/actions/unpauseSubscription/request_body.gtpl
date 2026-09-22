{{- $input := dict "subscriptionId" (.inputs.subscriptionId | toString) -}}
{{- if ne .inputs.isTemporaryPause nil -}}{{- $input = set $input "isTemporaryPause" (.inputs.isTemporaryPause) -}}{{- end -}}
{
  "query": "mutation unpauseSubscription($input: unpauseSubscriptionInput!) { unpauseSubscription(input: $input) { message ok } }",
  "variables": {
    "input": {{ toJson $input }}
  }
}
