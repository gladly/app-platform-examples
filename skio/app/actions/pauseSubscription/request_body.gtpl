{{- $input := dict "subscriptionId" (.inputs.subscriptionId | toString) -}}
{{- /* Provenance: stamp the caller into Skio's audit log. */ -}}
{{- $caller := "Gladly" -}}
{{- if and (ne .inputs.caller nil) (ne (.inputs.caller | toString | trim) "") -}}
    {{- $caller = printf "Gladly:%s" (.inputs.caller | toString | trim) -}}
{{- end -}}
{{- $input = set $input "debugCaller" $caller -}}
{{- /* skipOption was on Skio's input all along and no shipped action wired it up. Both
       halves are required together. `unit` is typed String! but Skio validates it as an enum:
       "unit must be a valid SellingPlanInterval or SkipOptionRelative" - confirmed live on
       2026-09-08, when a lowercase "week" was rejected. Hence the upper below. */ -}}
{{- if or (ne .inputs.skipUnit nil) (ne .inputs.skipValue nil) -}}
    {{- if or (eq .inputs.skipUnit nil) (eq .inputs.skipValue nil) -}}
        {{- stop "Pausing with a skip window needs both a unit and a value. Leave both blank for an open-ended pause." -}}
    {{- end -}}
    {{- $input = set $input "skipOption" (dict "unit" (.inputs.skipUnit | toString | trim | upper) "value" (.inputs.skipValue | float64)) -}}
{{- end -}}
{
  "query": "mutation pauseSubscription($input: pauseSubscriptionInput!) { pauseSubscription(input: $input) { message ok } }",
  "variables": {
    "input": {{ toJson $input }}
  }
}
