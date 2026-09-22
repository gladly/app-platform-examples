{{- /* The enableShipNow merchant gate runs in request_url.gtpl, with the other six. */ -}}
{{- $input := dict "subscriptionId" (.inputs.subscriptionId | toString) -}}
{{- /* Provenance: stamp the caller into Skio's audit log. */ -}}
{{- $caller := "Gladly" -}}
{{- if and (ne .inputs.caller nil) (ne (.inputs.caller | toString | trim) "") -}}
    {{- $caller = printf "Gladly:%s" (.inputs.caller | toString | trim) -}}
{{- end -}}
{{- $input = set $input "caller" $caller -}}
{
  "query": "mutation shipNow($input: ShipNowInput!) { shipNow(input: $input) { message ok } }",
  "variables": {
    "input": {{ toJson $input }}
  }
}
