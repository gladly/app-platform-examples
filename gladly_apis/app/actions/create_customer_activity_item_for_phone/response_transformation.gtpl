{{- if eq .response.statusCode 200 -}}
{
  "id": {{toJson .rawData.id}},
  "customerId": {{toJson .rawData.customerId}},
  "timestamp": {{toJson .rawData.timestamp}}
}
{{- else -}}
{
  {{- with .rawData.errors}}
  "errors": {{toJson .}}
  {{- end}}
}
{{- end -}}
