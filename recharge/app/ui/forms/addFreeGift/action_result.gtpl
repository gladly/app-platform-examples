{
{{- if .action.result.error}}
  "errors": [
    {
      "attr": "externalVariantId",
      "detail": {{ .action.result.error.error_message | toJson }}
    }
  ]
{{- else}}
  "message": "Free gift added.",
  "detail": {{ printf "Added free gift (variant %s, qty %s) to charge %s." .formAttrs.externalVariantId .formAttrs.quantity .formAttrs.chargeId | toJson }}
{{- end}}
}
