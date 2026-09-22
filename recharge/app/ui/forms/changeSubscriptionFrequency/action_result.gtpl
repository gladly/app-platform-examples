{
{{- if .action.result.error}}
  "errors": [
    {
      "attr": "frequencySelection",
      "detail": {{ .action.result.error.error_message | toJson }}
    }
  ]
{{- else}}
  {{- $sel := fromJson .formAttrs.frequencySelection}}
  "message": "Subscription frequency updated.",
  "detail": {{ printf "The subscription frequency is now %s." $sel.label | toJson }}
{{- end}}
}
