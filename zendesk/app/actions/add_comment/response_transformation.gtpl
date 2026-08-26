{{/* Handle different response status codes */}}
{{- if eq .response.statusCode 200 }}
{{/* Success - return the ticket data */}}
{
  "ticket": {
    "id": {{- template "idToString" .rawData.ticket.id}},
    "url": {{- toJson .rawData.ticket.url}},
    "subject": {{- toJson .rawData.ticket.subject}},
    "status": {{- toJson .rawData.ticket.status}},
    "priority": {{- toJson .rawData.ticket.priority}},
    "type": {{- toJson .rawData.ticket.type}},
    "created_at": {{- toJson .rawData.ticket.created_at}},
    "updated_at": {{- toJson .rawData.ticket.updated_at}},
    "due_at": {{- toJson .rawData.ticket.due_at}},
    "requester_id": {{- template "idToString" .rawData.ticket.requester_id}},
    "tags": {{- toJson .rawData.ticket.tags}},
    "group_id": {{- template "idToString" .rawData.ticket.group_id}},
    "ticket_form_id": {{- template "idToString" .rawData.ticket.ticket_form_id}}
  },
  "error": null
}
{{- else }}
{{/* All error responses - handle both standard and nested error formats */}}
{
  "ticket": null,
  "error": {
    {{- if kindIs "map" .rawData.error }}
    {{/* Nested format: {"error": {"title": "...", "message": "..."}} - used by 400 errors */}}
    "error": {{- toJson .rawData.error.title}},
    "description": {{- if .rawData.error.message }}{{- toJson .rawData.error.message}}{{- else }}"An error occurred"{{- end}}
    {{- else }}
    {{/* Standard format: {"error": "string", "description": "...", "details": {...}} */}}
    "error": {{- toJson .rawData.error}}
    {{- if .rawData.description -}}
    ,
    "description": {{- toJson .rawData.description}}
    {{- if .rawData.details -}}
    ,
    "details": {{- toJson (toJson .rawData.details)}}
    {{- end -}}
    {{- else if .rawData.message -}}
    ,
    "description": {{- toJson .rawData.message}}
    {{- else -}}
    ,
    "description": "HTTP {{.response.statusCode}}: {{.response.status}}"
    {{- end}}
    {{- end}}
  }
}
{{- end}}

{{define "idToString"}}
{{- $jsonResult := toJson . -}}
{{- if or (eq $jsonResult "null") (eq $jsonResult "") -}}
null{{- else -}}"{{$jsonResult}}"{{- end -}}
{{end}}
