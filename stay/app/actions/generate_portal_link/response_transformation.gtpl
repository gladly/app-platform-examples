{{- $code := .response.statusCode -}}

{{- if and (ge $code 200) (lt $code 300) -}}
    {{- $url := "" -}}
    {{- if kindIs "map" .rawData -}}
        {{- $url = coalesce (dig "url" nil .rawData) (dig "portalUrl" nil .rawData) (dig "link" nil .rawData) "" -}}
    {{- end -}}
    {{- if eq ($url | toString) "" -}}
        {{- fail "Stay.ai returned a success response without a portal link URL" -}}
    {{- end -}}
{ "error": null, "url": {{ $url | toJson }} }
{{- else if and (ge $code 400) (lt $code 500) -}}
    {{- $msg := .response.status -}}
    {{- if kindIs "map" .rawData -}}
        {{- $msg = coalesce (dig "message" nil .rawData) (dig "error" nil .rawData) $msg -}}
    {{- end -}}
    {{- if not (kindIs "string" $msg) -}}{{- $msg = $msg | toJson -}}{{- end -}}
{ "error": { "httpStatus": {{ $code }}, "message": {{ $msg | toJson }} }, "url": null }
{{- else -}}
    {{- fail (printf "unexpected response status %d from Stay.ai" $code) -}}
{{- end -}}
