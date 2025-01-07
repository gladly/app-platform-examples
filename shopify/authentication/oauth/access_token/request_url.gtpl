{{- template "verify_hmac" . -}}
{{- template "verify_nonce" . -}}

https://{{.integration.configuration.shop}}.myshopify.com/admin/oauth/access_token

{{- /* 
    verify the hmac from the query
    See: https://shopify.dev/docs/apps/build/authentication-authorization/get-access-tokens/auth-code-grant/implement-auth-code-grants-manually#step-1-verify-the-installation-request
*/ -}}
{{- define "verify_hmac" -}}
    {{- $redirectUrl := mustUrl .oauth.request.url}}
    {{- $queryParams := $redirectUrl.Query}}
    {{- $queryHmac := $queryParams.Get "hmac"}}
    {{- if eq $queryHmac ""}}
        {{- fail "the Shopify OAuth redirect does not contain an hmac query parameter"}}
    {{- end}}
    {{- /* create a list of query parameter key value pairs without the hmac parameter */}}
    {{- $queryParamList := list}}
    {{- range $key, $values := $queryParams}}
        {{- if ne $key "hmac"}}
            {{- $queryParamList = append $queryParamList (printf "%s=%s" $key (index $values 0))}}
        {{- end}}
    {{- end}}
    {{- /* sort the query parameters */}}
    {{- $sortedQueryParamList := sortAlpha $queryParamList}}
    {{- /* create the sorted query string without the hmac parameter */}}
    {{- $sortedQuery := $sortedQueryParamList | join "&"}}
    {{- $hmac := hmacSha256 .integration.secrets.client_secret "" $sortedQuery}}
    {{- if ne $hmac $queryHmac }}
        {{- fail (printf "unexpected hmac value specified in the Shopify OAuth redirect; expected %s" $hmac)}}
    {{- end}}
{{- end -}}

{{- /*
    verify the nonce from the query and the cookie
    See: https://shopify.dev/docs/apps/build/authentication-authorization/get-access-tokens/auth-code-grant/implement-auth-code-grants-manually#security-checks
*/ -}}
{{- define "verify_nonce"}}
    {{- $nonce := ""}}
    {{- range (.oauth.request.headers.Values "Cookie")}}
        {{- $nonce = regexFind "Gladly-OAuth-Nonce=\\w+" . | trimPrefix "Gladly-OAuth-Nonce="}}
        {{- if ne $nonce ""}}
            {{- break}}
        {{- end}}
    {{- end}}
    {{- if ne $nonce ""}}
        {{- if ne $nonce (hmacSha256 .integration.secrets.client_secret "" .oauth.state)}}
            {{- fail "invalid Gladly-OAuth-Nonce cookie value in Shopify OAuth redirect"}}
        {{- end}}
    {{- else}}
        {{- fail "Shopify OAuth redirect is missing Gladly-OAuth-Nonce cookie"}}
    {{- end}}
{{- end -}}