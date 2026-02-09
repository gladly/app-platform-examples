{{- /* Zendesk requires a non-empty name for user creation */ -}}
{{- if eq .inputs.user.name "" -}}
{{ stop "user name is required and cannot be empty" }}
{{- end -}}
{
  "user": {
    "name": "{{.inputs.user.name}}",
    "role": "end-user"
    {{- if .inputs.user.email}},
    "email": "{{.inputs.user.email}}"
    {{- end}}
    {{- if .inputs.user.phone}},
    "phone": "{{.inputs.user.phone}}"
    {{- end}}
  }
}
