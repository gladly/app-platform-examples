{{if .rawData.errors}}
{
  "errors": {{ toJson .rawData.errors }}
}
{{else if .rawData.data.cancelSubscription }}
{{- toJson .rawData.data.cancelSubscription -}}
{{/* Fallback for unexpected response structures */}}
{{else}}
fail "Unexpected response structure from Skio API for cancelSubscription"
{{end}}