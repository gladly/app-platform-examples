{{- /*
  Handle response from Zendesk create_or_update endpoint.
  - 200: User was found and updated
  - 201: User was created
  Both return the same user object structure.
*/ -}}
{{- if or (eq .response.statusCode 200) (eq .response.statusCode 201) }}
{
  "user": {
    {{- /* Zendesk returns id as a large number which Go renders in scientific notation (e.g., 1.2345e+10). Use printf %.0f to format as a plain integer string. */ -}}
    "id": "{{printf "%.0f" .rawData.user.id}}",
    "name": {{if .rawData.user.name}}{{.rawData.user.name | toJson}}{{else}}null{{end}},
    "email": {{if .rawData.user.email}}{{.rawData.user.email | toJson}}{{else}}null{{end}},
    "phone": {{if .rawData.user.phone}}{{.rawData.user.phone | toJson}}{{else}}null{{end}},
    "shared_phone_number": {{if .rawData.user.shared_phone_number}}true{{else}}false{{end}},
    "role": {{if .rawData.user.role}}{{.rawData.user.role | toJson}}{{else}}null{{end}},
    "verified": {{if .rawData.user.verified}}true{{else}}false{{end}},
    "active": {{if .rawData.user.active}}true{{else}}false{{end}},
    "suspended": {{if .rawData.user.suspended}}true{{else}}false{{end}},
    "created_at": {{if .rawData.user.created_at}}{{.rawData.user.created_at | toJson}}{{else}}null{{end}},
    "updated_at": {{if .rawData.user.updated_at}}{{.rawData.user.updated_at | toJson}}{{else}}null{{end}},
    "last_login_at": {{if .rawData.user.last_login_at}}{{.rawData.user.last_login_at | toJson}}{{else}}null{{end}},
    "external_id": {{if .rawData.user.external_id}}{{.rawData.user.external_id | toJson}}{{else}}null{{end}},
    "url": {{if .rawData.user.url}}{{.rawData.user.url | toJson}}{{else}}null{{end}}
  }
}
{{- else if or (eq .response.statusCode 400) (eq .response.statusCode 422) }}
{
  "error": {
    {{- /* 400 errors have nested error.title/message; 422 errors have flat error/description/details */ -}}
    {{- if kindIs "map" .rawData.error }}
    "error": {{ toJson .rawData.error.title }},
    "description": {{ toJson .rawData.error.message }}
    {{- else }}
    "error": {{ toJson .rawData.error }},
    "description": {{ toJson .rawData.description }},
    "details": {{ toJson .rawData.details }}
    {{- end }}
  }
}
{{- else }}
{
  "error": {
    "error": "UnexpectedError",
    "description": "Unexpected error: status {{ .response.statusCode }}"
  }
}
{{- end }}
