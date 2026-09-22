{
{{- if .action.result.error}}
  "errors": [
    {
      "attr": "rescheduleSelection",
      "detail": {{ .action.result.error.error_message | toJson }}
    }
  ]
{{- else}}
  {{- $sel := fromJson .formAttrs.rescheduleSelection}}
  "message": "Next charge rescheduled.",
  "detail": {{ printf "The subscription's next charge has been moved to %s." $sel.resumeDate | toJson }}
{{- end}}
}
