{{- $input := dict "subscriptionId" (.inputs.subscriptionId | toString) "code" (.inputs.code | toString | trim) -}}
{
  "query": "mutation applyDiscountCode($input: ApplyDiscountCodeInput!) { applyDiscountCode(input: $input) { message ok } }",
  "variables": {
    "input": {{ toJson $input }}
  }
}
