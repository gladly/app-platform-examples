{
{{- if .action.result.error}}
  "errors": [
    {
      "attr": "subscriptionId",
      "detail": {{ .action.result.error.error_message | toJson }}
    }
  ]
{{- else}}
  {{- $sub := .action.result.subscription}}
  {{- $isCancel := eq (.formAttrs.operation | toString) "cancel"}}
  {{- if $isCancel}}
  "message": "Subscription cancelled.",
  "detail": {{ printf "The subscription is now cancelled. Reason: %s." (default "n/a" (toString $sub.cancellation_reason)) | toJson }}
  {{- else}}
  "message": "Subscription reactivated.",
  "detail": {{ printf "The subscription is now active. Next charge: %s." (default "n/a" (toString $sub.next_charge_scheduled_at)) | toJson }}
  {{- end}}
{{- end}}
}
