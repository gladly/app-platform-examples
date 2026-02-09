{{/* Handle different response status codes */}}
{{- if eq .response.statusCode 201 }}
{{/* Success - return the ticket data with relevant fields */}}
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
    "assignee_id": {{- template "idToString" .rawData.ticket.assignee_id}},
    "group_id": {{- template "idToString" .rawData.ticket.group_id}},
    "ticket_form_id": {{- template "idToString" .rawData.ticket.ticket_form_id}}
  },
  "error": null
}
{{- else }}
{{/* All error responses */}}
{
  "ticket": null,
  "error": {
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
  }
}
{{- end}}

{{define "idToString"}}
{{- $jsonResult := toJson . -}}
{{- if or (eq $jsonResult "null") (eq $jsonResult "") -}}
null{{- else -}}"{{$jsonResult}}"{{- end -}}
{{end}}