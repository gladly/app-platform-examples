{{- $body := dict "amount" .inputs.amount "type" (.inputs.type | toString) -}}
{{- if and (ne .inputs.note nil) (ne .inputs.note "") -}}{{- $body = set $body "note" .inputs.note -}}{{- end -}}

{{ toJson $body }}
