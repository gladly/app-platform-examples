{
  "query": "mutation cancelSubscription($input: CancelSubscriptionInput!) { cancelSubscription(input: $input) { ok } }",
  "variables": {{ toJson .inputs}}
}