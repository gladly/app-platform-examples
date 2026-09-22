{{- $input := dict "subscriptionId" (.inputs.subscriptionId | toString) -}}
{{- if and (ne .inputs.billingInterval nil) (ne (.inputs.billingInterval | toString | trim) "") -}}{{- $input = set $input "billingInterval" (.inputs.billingInterval | toString | trim | upper) -}}{{- end -}}
{{- if ne .inputs.billingIntervalCount nil -}}{{- $input = set $input "billingIntervalCount" (.inputs.billingIntervalCount | int) -}}{{- end -}}
{{- if and (ne .inputs.nextBillingDate nil) (ne (.inputs.nextBillingDate | toString | trim) "") -}}{{- $input = set $input "nextBillingDate" (.inputs.nextBillingDate | toString | trim) -}}{{- end -}}
{{- if and (ne .inputs.prepaidDeliveryInterval nil) (ne (.inputs.prepaidDeliveryInterval | toString | trim) "") -}}{{- $input = set $input "prepaidDeliveryInterval" (.inputs.prepaidDeliveryInterval | toString | trim | upper) -}}{{- end -}}
{{- if ne .inputs.prepaidDeliveryIntervalCount nil -}}{{- $input = set $input "prepaidDeliveryIntervalCount" (.inputs.prepaidDeliveryIntervalCount | int) -}}{{- end -}}
{{- /* Provenance: stamp the caller into Skio's audit log. */ -}}
{{- $caller := "Gladly" -}}
{{- if and (ne .inputs.caller nil) (ne (.inputs.caller | toString | trim) "") -}}
    {{- $caller = printf "Gladly:%s" (.inputs.caller | toString | trim) -}}
{{- end -}}
{{- $input = set $input "debugCaller" $caller -}}
{
  "query": "mutation subscriptionEditInterval($input: SubscriptionEditIntervalInput!) { subscriptionEditInterval(input: $input) { subscriptionId } }",
  "variables": {
    "input": {{ toJson $input }}
  }
}
