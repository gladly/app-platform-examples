{{- /* see https://connect.gladly.com/docs/developer/article/request-signing/ for details */}}
{{- if .integration.secrets.signingKey}}

{{- $url := mustUrl .request.url}}
{{- $headers := dict}}
{{- $gladlyTime := ""}}

{{- /* headers with lower case header names and spaces trimmed from header name and value */}}
{{- range $headerName, $values := .request.headers}}
    {{- if gt (len $values) 0}}
        {{- $headers = set $headers (lower (trim $headerName)) (trim (index $values 0))}}
        {{- if eq $headerName "Gladly-Time"}}{{$gladlyTime = trim (index $values 0)}}{{end}}
    {{- end}}
{{- end}}

{{- /* list of header names to be sorted */}}
{{- $headerNames := list}}
{{- range $headerName, $value := $headers}}
    {{- $headerNames = append $headerNames $headerName}}
{{- end}}

{{- /* sort the headers alphabetically */}}
{{- $sortedHeaderNames := sortAlpha $headerNames}}

{{- $normalizedRequest := list}}
{{- /* HTTP method */}}
{{- $normalizedRequest = append $normalizedRequest .request.method}}
{{- /* URL path */}}
{{- $normalizedRequest = append $normalizedRequest $url.Path}}
{{- /* sorted query parameters */}}
{{- $normalizedRequest = append $normalizedRequest $url.Query.Encode}}
{{- /* sorted headers and values*/}}
{{- range $headerName := $sortedHeaderNames}}
    {{- $normalizedRequest = append $normalizedRequest (printf "%s:%s" $headerName (get $headers $headerName))}}
{{- end}}
{{- /* blank line */}}
{{- $normalizedRequest = append $normalizedRequest ""}}
{{- /* sorted header names */}}
{{- $normalizedRequest = append $normalizedRequest (join ";" $sortedHeaderNames)}}
{{- /* lower case hex of sha256 request body */}}
{{- $normalizedRequest = append $normalizedRequest (lower (sha256sum .request.body))}}

{{- $sha256NormalizedRequest := lower (sha256sum (join "\n" $normalizedRequest))}}
{{- $stringToSign := join "\n" (list "hmac-sha256" $gladlyTime $sha256NormalizedRequest)}}
{{- $salt := date "20060102" (mustToDate "20060102T150405Z" $gladlyTime)}}
{{- $signature := mustHmacSha256 .integration.secrets.signingKey $salt $stringToSign -}}

SigningAlgorithm=hmac-sha256, SignedHeaders={{join ";" $sortedHeaderNames}}, Signature={{$signature}}

{{- end}}