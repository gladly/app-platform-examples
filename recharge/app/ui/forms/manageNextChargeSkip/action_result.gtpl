{
{{- if .action.result.error}}
  "errors": [
    {
      "attr": "chargeSelection",
      "detail": {{ .action.result.error.error_message | toJson }}
    }
  ]
{{- else}}
  {{- $charge := .action.result.charge}}
  {{- $isSkip := eq (.formAttrs.operation | toString) "skip"}}
  {{- if $isSkip}}
  "message": "Next order skipped.",
  "detail": {{ printf "The order scheduled %s has been skipped." (default "n/a" (toString $charge.scheduled_at)) | toJson }}
  {{- else}}
  "message": "Next order unskipped.",
  "detail": {{ printf "The order scheduled %s has been restored to the queue." (default "n/a" (toString $charge.scheduled_at)) | toJson }}
  {{- end}}
{{- end}}
}
