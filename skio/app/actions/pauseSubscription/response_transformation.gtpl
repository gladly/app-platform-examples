{{if .rawData.errors}}
{
  "ok": false,
  "errors": {{ toJson .rawData.errors }}
}
{{else if .rawData.data.pauseSubscription }}
  {{- toJson .rawData.data.pauseSubscription -}}
{{/* Fallback for unexpected response structures */}}
{{else}}
fail "Unexpected response structure from Skio API for pauseSubscription"
{{end}}
