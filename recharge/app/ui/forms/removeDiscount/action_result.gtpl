{
{{- if .action.result.error}}
  "errors": [
    {
      "attr": "chargeId",
      "detail": {{ .action.result.error.error_message | toJson }}
    }
  ]
{{- else}}
  {{- $charge := .action.result.charge}}
  {{- $date := default "n/a" $charge.scheduled_at}}
  "message": "Discount removed.",
  "detail": {{ printf "Removed the discount from the order shipping %s." $date | toJson }}
{{- end}}
}
