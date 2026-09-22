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
    {{- $detail = .action.result.message | default "Skio would not reactivate this subscription. Either it is already active, or it was permanently cancelled - which cannot be undone. Check its status on the Skio card." | toString -}}
{{- end -}}
{{- if ne $detail "" -}}
{
  "errors": [
    {
      "attr": "newBillingDate",
      "detail": {{ $detail | toJson }}
    }
  ]
}
{{- else -}}
{{- $detailText := printf "The subscription is active again and next bills on %s." (.action.inputs.newBillingDate | toString) -}}
{
  "message": "Subscription reactivated.",
  "detail": {{ $detailText | toJson }}
}
{{- end -}}
