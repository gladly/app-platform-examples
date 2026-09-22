{{- $input := dict "subscriptionId" (.inputs.subscriptionId | toString) "date" (.inputs.date | toString | trim) -}}
{{- /* Provenance: stamp the caller into Skio's audit log. */ -}}
{{- $caller := "Gladly" -}}
{{- if and (ne .inputs.caller nil) (ne (.inputs.caller | toString | trim) "") -}}
    {{- $caller = printf "Gladly:%s" (.inputs.caller | toString | trim) -}}
{{- end -}}
{{- $input = set $input "debugCaller" $caller -}}
{
  "query": "mutation updateNextBillingDate($input: UpdateNextBillingDateInput!) { updateNextBillingDate(input: $input) { message ok } }",
  "variables": {
    "input": {{ toJson $input }}
  }
}
