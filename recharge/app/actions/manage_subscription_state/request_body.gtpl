{{- $isCancel := eq (.inputs.operation | toString) "cancel" -}}

{{- if $isCancel -}}
{{- $defaultReason := "Cancellation reason was not specified" -}}
{{- $hasReason := and (ne .inputs.reason "") (ne .inputs.reason nil) -}}
{{- $sendEmailProvided := and (ne .inputs.sendEmail "") (ne .inputs.sendEmail nil) -}}
{{- $sendEmailBool := ($sendEmailProvided | ternary (eq .inputs.sendEmail "true") true) -}}
{
    "cancellation_reason": {{ ($hasReason | ternary .inputs.reason $defaultReason) | toJson }},
    "send_email": {{- $sendEmailBool | toJson -}}
}
{{- else -}}
{{- /* reactivate ignores reason/sendEmail -- they are cancel-only fields (see form hint) */ -}}
{}
{{- end -}}
