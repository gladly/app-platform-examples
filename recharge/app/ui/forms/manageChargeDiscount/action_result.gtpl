{
{{- if .action.result.error}}
  {{- $isApply := eq (.formAttrs.operation | toString) "apply"}}
  {{- if $isApply}}
  {{- /* Recharge apply-discount errors are inconsistently punctuated; strip a trailing period so our
         own ". Check..." suffix doesn't double up (OBS-3), matching the old applyDiscount form. */ -}}
  {{- $clean := trimSuffix "." .action.result.error.error_message}}
  "errors": [
    {
      "attr": "discountCode",
      "detail": {{ printf "Recharge couldn't apply this code: %s. Check the spelling and that the promotion is active for this customer in Recharge." $clean | toJson }}
    }
  ]
  {{- else}}
  "errors": [
    {
      "attr": "chargeId",
      "detail": {{ .action.result.error.error_message | toJson }}
    }
  ]
  {{- end}}
{{- else}}
  {{- $charge := .action.result.charge}}
  {{- $date := default "n/a" $charge.scheduled_at}}
  {{- $isApply := eq (.formAttrs.operation | toString) "apply"}}
  {{- if $isApply}}
  "message": "Discount applied.",
  "detail": {{ printf "Applied %s to the order shipping %s." .formAttrs.discountCode $date | toJson }}
  {{- else}}
  "message": "Discount removed.",
  "detail": {{ printf "Removed the discount from the order shipping %s." $date | toJson }}
  {{- end}}
{{- end}}
}
