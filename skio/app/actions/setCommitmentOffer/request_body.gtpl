{{- $input := dict "subscriptionId" (.inputs.subscriptionId | toString) "additionalCycles" (.inputs.additionalCycles | int) -}}
{
  "query": "mutation setCommitmentOffer($input: SetCommitmentOfferInput!) { setCommitmentOffer(input: $input) { commitmentLength commitmentStartCycle minimumCancelCycle subscriptionId } }",
  "variables": {
    "input": {{ toJson $input }}
  }
}
