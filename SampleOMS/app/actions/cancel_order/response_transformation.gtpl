{{/* Check if the response is empty (404 Not Found) */}}
{{- if eq .response.statusCode 404 }}
{
  "status": "error",
  "message": "Order not found."
}
{{- else }}
{{- toJson .rawData.data -}}
{{- end}}