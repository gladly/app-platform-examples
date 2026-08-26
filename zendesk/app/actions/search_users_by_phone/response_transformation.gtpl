{{- if eq .response.statusCode 200 -}}
{{- /*
  Filter to only include active users.
  Zendesk's search API only supports filtering by is_suspended, not by active status.
  Deleted users (active=false) can still appear in search results, so we filter them here.
*/ -}}
{{- $activeUsers := list -}}
{{- range $user := .rawData.users -}}
  {{- if $user.active -}}
    {{- $activeUsers = append $activeUsers $user -}}
  {{- end -}}
{{- end -}}
{
  "count": {{len $activeUsers}},
  "users": [
    {{- range $i, $user := $activeUsers}}
    {{if $i}},{{end}}
    {
      "id": "{{printf "%.0f" $user.id}}",
      "name": {{if $user.name}}{{$user.name | toJson}}{{else}}null{{end}},
      "email": {{if $user.email}}{{$user.email | toJson}}{{else}}null{{end}},
      "phone": {{if $user.phone}}{{$user.phone | toJson}}{{else}}null{{end}},
      "shared_phone_number": {{if $user.shared_phone_number}}true{{else}}false{{end}},
      "role": {{if $user.role}}{{$user.role | toJson}}{{else}}null{{end}},
      "verified": {{if $user.verified}}true{{else}}false{{end}},
      "active": {{if $user.active}}true{{else}}false{{end}},
      "suspended": {{if $user.suspended}}true{{else}}false{{end}},
      "created_at": {{if $user.created_at}}{{$user.created_at | toJson}}{{else}}null{{end}},
      "updated_at": {{if $user.updated_at}}{{$user.updated_at | toJson}}{{else}}null{{end}},
      "last_login_at": {{if $user.last_login_at}}{{$user.last_login_at | toJson}}{{else}}null{{end}},
      "external_id": {{if $user.external_id}}{{$user.external_id | toJson}}{{else}}null{{end}},
      "url": {{if $user.url}}{{$user.url | toJson}}{{else}}null{{end}}
    }
    {{- end}}
  ],
  "error": null
}
{{- else -}}
{
  "count": 0,
  "users": [],
  "error": {
    "error": "{{.rawData.error}}",
    "description": {{if .rawData.description}}{{.rawData.description | toJson}}{{else}}"Search failed with status {{.response.statusCode}}"{{end}},
    "details": {{if .rawData.details}}{{.rawData.details | toJson}}{{else}}null{{end}}
  }
}
{{- end -}}
