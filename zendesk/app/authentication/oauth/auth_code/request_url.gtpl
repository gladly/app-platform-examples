{{- /*
    Zendesk OAuth 2.0 Authorization Code Flow

    Required scopes for Zendesk Support API:
    - tickets:read - Read tickets
    - tickets:write - Create/update tickets
    - users:read - Read users (for requester information)
    - users:write - Create/update users
    - hc:read - Read Help Center content

    More about scopes: https://developer.zendesk.com/documentation/ticketing/working-with-oauth/
*/ -}}
https://{{.integration.configuration.subdomain}}.zendesk.com/oauth/authorizations/new?response_type=code&client_id={{.integration.configuration.client_id}}&redirect_uri={{urlquery .oauth.redirect_uri}}&scope=tickets:read%20tickets:write%20users:read%20users:write%20hc:read&state={{.correlationId}}
