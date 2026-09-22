{{- $input := dict "subscriptionId" (.inputs.subscriptionId | toString) -}}
{{- /* Provenance: stamp the caller into Skio's audit log. */ -}}
{{- $caller := "Gladly" -}}
{{- if and (ne .inputs.caller nil) (ne (.inputs.caller | toString | trim) "") -}}
    {{- $caller = printf "Gladly:%s" (.inputs.caller | toString | trim) -}}
{{- end -}}
{{- $input = set $input "debugCaller" $caller -}}
{{- /* skipOption moves the billing date by an explicit window instead of one cycle.
       Both halves are required together. `unit` is String! but enum-validated by Skio and
       are pending live confirmation, so this is an advanced, optional path. */ -}}
{{- if or (ne .inputs.skipUnit nil) (ne .inputs.skipValue nil) -}}
    {{- if or (eq .inputs.skipUnit nil) (eq .inputs.skipValue nil) -}}
        {{- stop "Skipping by a window needs both a unit and a value. Leave both blank to skip exactly one cycle." -}}
    {{- end -}}
    {{- $input = set $input "skipOption" (dict "unit" (.inputs.skipUnit | toString | trim | upper) "value" (.inputs.skipValue | float64)) -}}
{{- end -}}
{
  "query": "mutation skipSubscription($input: SkipSubscriptionInput!) { skipSubscription(input: $input) { message nextBillingDate ok } }",
  "variables": {
    "input": {{ toJson $input }}
  }
}
