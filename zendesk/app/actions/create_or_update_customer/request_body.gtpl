{{- /*
  Create or update a Zendesk user using the Gladly customer ID as external_id.

  Zendesk's create_or_update endpoint matches on:
  1. email (if provided)
  2. external_id (if provided)

  By using Gladly customer ID as external_id, we ensure:
  - First contact: Creates user with external_id set to Gladly ID
  - Return contact: Matches on external_id, updates user (no-op if unchanged)
  - Phone-only customers work because matching is on external_id, not phone
*/ -}}
{{- if eq .inputs.user.name "" -}}
{{ stop "user name is required and cannot be empty" }}
{{- end -}}
{
  "user": {
    {{- if .inputs.user.customerId}}
    "external_id": "{{.inputs.user.customerId}}",
    {{- end}}
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
