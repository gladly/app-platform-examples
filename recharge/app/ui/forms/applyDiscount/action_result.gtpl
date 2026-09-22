{
{{- if .action.result.error}}
  {{- $raw := .action.result.error.error_message}}
  {{- /* Recharge error messages are inconsistent: some end with a period ("You already applied a
         discount code to the charge."), some don't ("Discount not found"). Strip a trailing period
         so our own ". Check..." suffix doesn't produce a double period (OBS-3). Recharge's
         period-ending messages have no trailing space, so a plain trimSuffix is enough. */ -}}
  {{- $clean := trimSuffix "." $raw}}
  {{- $detail := printf "Recharge couldn't apply this code: %s. Check the spelling and that the promotion is active for this customer in Recharge." $clean}}
  "errors": [
    {
      "attr": "discountCode",
      "detail": {{ $detail | toJson }}
    }
  ]
{{- else}}
  {{- $charge := .action.result.charge}}
  {{- $date := default "n/a" $charge.scheduled_at}}
  "message": "Discount applied.",
  "detail": {{ printf "Applied %s to the order shipping %s." .formAttrs.discountCode $date | toJson }}
{{- end}}
}
