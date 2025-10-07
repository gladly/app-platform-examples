{
{{- if and .inputs.cancelReasonCode .inputs.cancelReasonDetails }}
    "cancel_reason": "{{.inputs.cancelReasonCode}}|{{.inputs.cancelReasonDetails}}"
{{- end }}
}
