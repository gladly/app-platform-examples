{{- $consumerKey := .integration.secrets.consumer_key -}}
{{- $token := .integration.secrets.access_token -}}
{{- $consumerSecret := .integration.secrets.consumer_secret -}}
{{- $tokenSecret := .integration.secrets.access_token_secret -}}
{{- $nonce := randAlphaNum 32 -}}
{{- $timestamp := now | unixEpoch | toString -}}
{{/* Parse request URL into base URL and query parameters */}}
{{- $parsedUrl := mustUrl .request.url -}}
{{- $baseUrl := printf "%s://%s%s" $parsedUrl.Scheme $parsedUrl.Host $parsedUrl.Path -}}
{{/* Build a list of percent-encoded key=value pairs for all parameters */}}
{{- $paramPairs := list -}}
{{/* Add OAuth parameters */}}
{{- $paramPairs = append $paramPairs (printf "%s=%s" (urlquery "oauth_consumer_key") (urlquery $consumerKey)) -}}
{{- $paramPairs = append $paramPairs (printf "%s=%s" (urlquery "oauth_nonce") (urlquery $nonce)) -}}
{{- $paramPairs = append $paramPairs (printf "%s=%s" (urlquery "oauth_signature_method") (urlquery "HMAC-SHA256")) -}}
{{- $paramPairs = append $paramPairs (printf "%s=%s" (urlquery "oauth_timestamp") (urlquery $timestamp)) -}}
{{- $paramPairs = append $paramPairs (printf "%s=%s" (urlquery "oauth_token") (urlquery $token)) -}}
{{- $paramPairs = append $paramPairs (printf "%s=%s" (urlquery "oauth_version") (urlquery "1.0")) -}}
{{/* Add query parameters from URL using decoded .Query to avoid double-encoding */}}
{{- range $key, $values := $parsedUrl.Query -}}
    {{- range $values -}}
        {{- $paramPairs = append $paramPairs (printf "%s=%s" (urlquery $key) (urlquery .)) -}}
    {{- end -}}
{{- end -}}
{{/* Sort all parameter pairs and build parameter string */}}
{{- $sortedPairs := sortAlpha $paramPairs -}}
{{- $paramString := join "&" $sortedPairs -}}
{{/* Build signature base string: METHOD&encoded_base_url&encoded_param_string */}}
{{- $signatureBaseString := printf "%s&%s&%s" (upper .request.method) (urlquery $baseUrl) (urlquery $paramString) -}}
{{/* Build signing key: encoded_consumer_secret&encoded_token_secret */}}
{{- $signingKey := printf "%s&%s" (urlquery $consumerSecret) (urlquery $tokenSecret) -}}
{{/* Generate HMAC-SHA256 signature: convert hex to bytes then base64 encode */}}
{{- $signature := mustHmacSha256 $signingKey "" $signatureBaseString | hexDec | printf "%s" | b64enc -}}
{{/* Output OAuth Authorization header with percent-encoded values */}}
OAuth oauth_consumer_key="{{urlquery $consumerKey}}",oauth_nonce="{{urlquery $nonce}}",oauth_signature="{{urlquery $signature}}",oauth_signature_method="HMAC-SHA256",oauth_timestamp="{{urlquery $timestamp}}",oauth_token="{{urlquery $token}}",oauth_version="1.0"