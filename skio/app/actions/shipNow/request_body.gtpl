{
  "query": "mutation shipNow($input: ShipNowInput!) { shipNow(input: $input) { message ok } }",
  "variables": {
    "input": {
      "subscriptionId": "{{.inputs.subscriptionId}}"
    }
  }
} 