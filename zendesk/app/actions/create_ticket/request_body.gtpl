{{- /* Validate that exactly one of requester or requester_id is provided */ -}}
{{- if and .inputs.requester .inputs.requester_id -}}
  {{- stop "Cannot specify both 'requester' and 'requester_id'. Use 'requester' to create a new requester or 'requester_id' to reference an existing user." -}}
{{- end -}}
{{- if and (not .inputs.requester) (not .inputs.requester_id) -}}
  {{- stop "Must specify either 'requester' or 'requester_id'. Use 'requester' to create a new requester or 'requester_id' to reference an existing user." -}}
{{- end -}}
{
  "ticket": {
    "subject": "{{.inputs.subject}}",
    "comment": {
      {{if .inputs.comment.html_body}}"html_body": "{{.inputs.comment.html_body}}"{{else}}"body": "{{.inputs.comment.body}}"{{end}},
      "public": {{.inputs.comment.public}}{{if .inputs.comment.author_id}},
      "author_id": {{.inputs.comment.author_id}}{{end}}
    },
    {{- if .inputs.requester}}
    "requester": {
      "name": "{{.inputs.requester.name}}",
      "email": "{{.inputs.requester.email}}"
    }{{else}}
    "requester_id": {{.inputs.requester_id}}{{end}}{{if .inputs.submitter_id}},
    "submitter_id": {{.inputs.submitter_id}}{{end}},
    "priority": "{{if .inputs.priority}}{{.inputs.priority}}{{else}}normal{{end}}",
    "status": "{{if .inputs.status}}{{.inputs.status}}{{else}}new{{end}}",
    "type": "{{if .inputs.type}}{{.inputs.type}}{{else}}incident{{end}}"{{if .inputs.tags}},
    "tags": [{{range $i, $tag := .inputs.tags}}{{if $i}}, {{end}}"{{$tag}}"{{end}}]{{end}}{{if .inputs.assignee_id}},
    "assignee_id": {{.inputs.assignee_id}}{{end}}{{if .inputs.group_id}},
    "group_id": {{.inputs.group_id}}{{end}}{{if .inputs.ticket_form_id}},
    "ticket_form_id": {{.inputs.ticket_form_id}}{{end}}{{if .inputs.channel}},
    "via": {"channel": "{{.inputs.channel}}"}{{end}}
  }
}
