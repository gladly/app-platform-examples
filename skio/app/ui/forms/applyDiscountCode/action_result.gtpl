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
      "attr": "confirmed",
      "detail": {{ $detail | toJson }}
    }
  ]
}
{{- else -}}
{{- $detailText := printf "Discount %s was applied to the subscription." (.action.inputs.code | toString) -}}
{{- $msg := .action.result.message | default "" | toString -}}
{{- if ne $msg "" -}}{{- $detailText = printf "%s %s" $detailText $msg -}}{{- end -}}
{
  "message": "Discount applied.",
  "detail": {{ $detailText | toJson }}
}
{{- end -}}
