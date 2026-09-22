{{- /* Failure is whatever the action layer reports: response_transformation.gtpl sets
       .action.result.errorMessage to the agent-facing text when the action failed and leaves it
       absent on success. Skio's untranslated originals stay on .action.result.errors. */ -}}
{{- $detail := .action.result.errorMessage | default "" | toString -}}
{{- if and (eq $detail "") (eq (printf "%v" .action.result.ok) "false") -}}
    {{- /* A payload that reports ok:false without an errorMessage: say what it said. */ -}}
    {{- $detail = .action.result.message | default "Skio rejected the change." | toString -}}
{{- end -}}
{{- if ne $detail "" -}}
{
  "errors": [
    {
      "attr": "phone",
      "detail": {{ $detail | toJson }}
    }
  ]
}
{{- else -}}
{{- $onFile := .action.inputs.newShippingAddressPlatformId | default "" | toString -}}
{{- $detailText := "The subscription now ships to the new address." -}}
{{- if ne $onFile "" -}}
    {{- $detailText = "The subscription now ships to an address the customer already had on file." -}}
{{- end -}}
{
  "message": "Shipping address changed.",
  "detail": {{ $detailText | toJson }}
}
{{- end -}}
