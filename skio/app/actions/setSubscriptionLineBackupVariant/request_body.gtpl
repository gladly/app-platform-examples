{{- $input := dict "subscriptionId" (.inputs.subscriptionId | toString) "subscriptionLineId" (.inputs.subscriptionLineId | toString) "backupVariantId" (.inputs.backupVariantId | toString) -}}
{
  "query": "mutation setSubscriptionLineBackupVariant($input: SetSubscriptionLineBackupVariantInput!) { setSubscriptionLineBackupVariant(input: $input) { ok } }",
  "variables": {
    "input": {{ toJson $input }}
  }
}
