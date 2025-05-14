{{if .rawData.errors}}
{
  "ok": false,
  "errors": {{ toJson .rawData.errors }}
}
{{else if .rawData.data.shipNow}}
  {{- toJson .rawData.data.shipNow -}}
{{else}}
fail "Unexpected response structure from Skio API for shipNow"
{{end}} 