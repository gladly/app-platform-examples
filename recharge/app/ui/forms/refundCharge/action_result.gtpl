{
{{- if .action.result.error}}
  "errors": [
    {
      "attr": "confirmationCopy",
      "detail": {{ .action.result.error.error_message | toJson }}
    }
  ]
{{- else}}
  {{- $charge := .action.result.charge}}
  {{- $isFull := eq (.formAttrs.fullRefund | toString) "true"}}
  {{- if $isFull}}
  "message": "Refund issued.",
  "detail": {{ printf "Full refund issued. Total refunded on this order is now %s." (default "n/a" (toString $charge.total_refunds)) | toJson }}
  {{- else}}
  "message": "Refund issued.",
  "detail": {{ printf "Refunded %s. Total refunded on this order is now %s." (default "n/a" (toString .formAttrs.amount)) (default "n/a" (toString $charge.total_refunds)) | toJson }}
  {{- end}}
{{- end}}
}
