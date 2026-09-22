{
{{- if .action.result.error}}
  "errors": [
    {
      "attr": "confirmationCopy",
      "detail": {{ .action.result.error.error_message | toJson }}
    }
  ]
{{- else}}
  {{- $adj := .action.result.credit_adjustment}}
  "message": "Credit adjusted.",
  "detail": {{ printf "%s of %s applied. New balance: %s." (toString $adj.type) (toString $adj.amount) (toString $adj.ending_balance) | toJson }}
{{- end}}
}
