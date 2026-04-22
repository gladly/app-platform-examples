{
  "query": "mutation cancelSubscription($input: CancelSubscriptionInput!) { cancelSubscription(input: $input) { ok } }",
  "variables": {
    "input": {
      "subscriptionId": {{ toJson .inputs.subscriptionId }}{{ if .inputs.shouldSendNotif }},
      "shouldSendNotif": {{ toJson .inputs.shouldSendNotif }}{{ end }}
    }
  }
}