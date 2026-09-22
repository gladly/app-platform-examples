{{- $input := dict "subscriptionId" (.inputs.subscriptionId | toString) "newBillingDate" (.inputs.newBillingDate | toString | trim) -}}
{
  "query": "mutation reactivateSubscription($input: ReactivateSubscriptionInput!) { reactivateSubscription(input: $input) { ok } }",
  "variables": {
    "input": {{ toJson $input }}
  }
}
