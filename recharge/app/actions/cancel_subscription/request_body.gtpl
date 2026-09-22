{{- $defaultReason := "Cancellation reason was not specified" -}}

{{- $hasReason := and (ne .inputs.reason "") (ne .inputs.reason nil) -}}
{{- $sendEmail := ne .inputs.sendEmail nil -}}

{
    "cancellation_reason": "{{- $hasReason | ternary .inputs.reason $defaultReason -}}",
    "send_email": {{- ($sendEmail | ternary .inputs.sendEmail true) | toJson -}}
}
