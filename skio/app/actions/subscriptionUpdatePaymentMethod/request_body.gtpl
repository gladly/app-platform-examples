{{- $input := dict "subscriptionId" (.inputs.subscriptionId | toString) "paymentMethodPlatformId" (.inputs.paymentMethodPlatformId | toString | trim) -}}
{
  "query": "mutation subscriptionUpdatePaymentMethod($input: SubscriptionUpdatePaymentMethodInput!) { subscriptionUpdatePaymentMethod(input: $input) { subscriptionId } }",
  "variables": {
    "input": {{ toJson $input }}
  }
}
