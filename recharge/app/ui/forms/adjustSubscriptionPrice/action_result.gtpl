{
{{- if .action.result.error}}
  {{- $msg := .action.result.error.error_message}}
  {{- /* The confirmation guard's message is the only one of the four in request_url.gtpl that's
         a static literal (the others interpolate $price/$capRaw) -- match it with an exact `eq`,
         not `contains`, since a substring/case-fold check risks differing between the offline
         appcfg test engine and the live cloud runtime (confirmed live 2026-07-01: `contains
         "approve" (lower $msg)` anchored the over-cap error to confirmationCopy in production
         despite passing golden tests with attr "price"). */ -}}
  {{- if eq $msg "Type approve in the confirmation field to submit this price change."}}
  "errors": [
    {
      "attr": "confirmationCopy",
      "detail": {{ $msg | toJson }}
    }
  ]
  {{- else}}
  "errors": [
    {
      "attr": "price",
      "detail": {{ $msg | toJson }}
    }
  ]
  {{- end}}
{{- else}}
  {{- $sub := .action.result.subscription}}
  "message": "Subscription price updated.",
  "detail": {{ printf "The subscription price is now %s." (toString $sub.price) | toJson }}
{{- end}}
}
