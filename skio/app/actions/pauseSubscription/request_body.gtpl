{
  "query": "mutation PauseSubscription($subscriptionId: String!) { pauseSubscription(input: {subscriptionId: $subscriptionId}) { message ok } }",
  "variables": {
    "subscriptionId": "{{.inputs.subscriptionId}}"
  }
}
