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
      "attr": "returnToPath",
      "detail": {{ $detail | toJson }}
    }
  ]
}
{{- else -}}
{{- $url := .action.result.magicLinkUrl | default "" | toString -}}
{{- $expires := .action.result.expiresAt | default "" | toString -}}
{{- $detailText := "Skio created a passwordless link for this customer." -}}
{{- if ne $url "" -}}
    {{- if ne $expires "" -}}
        {{- $detailText = printf "%s - expires %s. Send it to the customer before then; an expired link will not work." $url $expires -}}
    {{- else -}}
        {{- $detailText = $url -}}
    {{- end -}}
{{- else if ne (.action.result.error | default "" | toString) "" -}}
    {{- $detailText = .action.result.error | toString -}}
{{- end -}}
{
  "message": "Account portal link created.",
  "detail": {{ $detailText | toJson }}
}
{{- end -}}
