{{- /* Gladly AI and Team Assist call these actions directly and never render a form, so the
       translation of Skio's misleading errors lives in the action layer, where every caller
       gets it. The action sets `errorMessage` to the agent-facing text on every failure - both
       of Skio's shapes, a top-level `errors` array and a 200 whose payload carries `ok: false` -
       and leaves it unset on success. `.action.result.errors` keeps Skio's untranslated
       original. Read the translation; never re-translate here. */ -}}
{{- $detail := .action.result.errorMessage | default "" | toString -}}
{{- if and (eq $detail "") (eq (printf "%v" .action.result.ok) "false") -}}
    {{- /* Defensive: a payload that reports `ok: false` with no translation. Report Skio's own
           message verbatim - a failure must never reach the timeline as a success. */ -}}
    {{- $detail = .action.result.message | default "Skio rejected the change." | toString -}}
{{- end -}}
{{- if ne $detail "" -}}
{
  "errors": [
    {
      "attr": "caller",
      "detail": {{ $detail | toJson }}
    }
  ]
}
{{- else -}}
{{- $interval := .action.inputs.billingInterval | default "" | toString -}}
{{- $count := .action.inputs.billingIntervalCount | default "" | toString -}}
{{- $detailText := "The subscription cadence was updated in Skio." -}}
{{- if and (ne $interval "") (ne $count "") -}}
    {{- $detailText = printf "The subscription now bills every %s %s(s)." $count (lower $interval) -}}
{{- else if ne $interval "" -}}
    {{- $detailText = printf "The subscription now bills on a %s cadence." (lower $interval) -}}
{{- end -}}
{
  "message": "Subscription frequency changed.",
  "detail": {{ $detailText | toJson }}
}
{{- end -}}
