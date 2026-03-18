{
  "client_id": "{{.integration.configuration.client_id}}",
  "client_secret": "{{.integration.secrets.client_secret}}",
  "grant_type": "authorization_code",
  "code": "{{.oauth.code}}",
  "redirect_uri": {{toJson .oauth.redirect_uri}}
}
