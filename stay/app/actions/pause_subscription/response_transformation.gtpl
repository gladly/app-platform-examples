{{- $code := .response.statusCode -}}

{{- if and (ge $code 200) (lt $code 300) -}}
    {{- /* success: return the subscription's state so the timeline records what it became */ -}}
    {{- $sub := dict "id" (.inputs.subscriptionId | toString) -}}
    {{- if kindIs "map" .rawData -}}
        {{- $raw := .rawData -}}
        {{- $id := dig "id" nil $raw -}}
        {{- if $id -}}
            {{- $id = kindIs "float64" $id | ternary ($id | int64 | toString) ($id | toString) -}}
            {{- $_ := set $sub "id" $id -}}
        {{- end -}}
        {{- $status := dig "status" nil $raw -}}
        {{- $paused := dig "pausedUntil" nil $raw -}}
        {{- if kindIs "map" $status -}}
            {{- $paused = $status.pausedUntil -}}
            {{- $status = $status.status -}}
        {{- end -}}
        {{- $_ := set $sub "status" $status -}}
        {{- $_ := set $sub "pausedUntil" $paused -}}
        {{- $_ := set $sub "nextBillingDate" (dig "nextBillingDate" nil $raw) -}}
    {{- end -}}
{ "error": null, "subscription": {{ $sub | toJson }} }
{{- else if and (ge $code 400) (lt $code 500) -}}
    {{- /* Stay-side rejections (bad input, unknown id, state conflict, rate limit) map to a
           legible error; 401/403 never reach this template (platform-handled) */ -}}
    {{- $msg := .response.status -}}
    {{- if kindIs "map" .rawData -}}
        {{- $msg = coalesce (dig "message" nil .rawData) (dig "error" nil .rawData) $msg -}}
    {{- end -}}
    {{- if not (kindIs "string" $msg) -}}{{- $msg = $msg | toJson -}}{{- end -}}
{ "error": { "httpStatus": {{ $code }}, "message": {{ $msg | toJson }} }, "subscription": null }
{{- else -}}
    {{- fail (printf "unexpected response status %d from Stay.ai" $code) -}}
{{- end -}}
