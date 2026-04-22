{
  "grant_type": "authorization_code",
  "code": "{{.oauth.code}}",
  "client_id": "{{.integration.configuration.client_id}}",
  "client_secret": "{{.integration.secrets.client_secret}}",
  "redirect_uri": "{{.oauth.redirect_uri}}",
  "scope": "tickets:read tickets:write users:read users:write hc:read"
}
