{{/* request errors https://www.aftership.com/docs/tracking/quickstart/request-errors */}}
{{- if not ( list 200 201 400 | has .response.statusCode ) }}
    {{ $statusCodeFamily := print .response.statusCode | printf "%.1s" }}

    {{ $errReason := "" }}
    {{ with .rawData.meta }}
        {{ $errReason = printf "%v - %s" .code .message }}
    {{ end }}

    {{ printf "Unsuccessful Request. HTTP Status: %d, Reason: %s" .response.statusCode $errReason | fail }}
{{ else -}}
{ 
  {{- with .rawData.meta -}}
  {{- if not ( list 200 201 | has ( int .code ) ) }}
    "error": {
        "code": {{ .code }},
        "message": "{{ .message }}",
        "type": "{{ .type }}"
    },
  {{- else }}
    "error": null,
  {{- end -}}
  {{- end -}}
  {{- with .rawData.data.trackings }}
    "tracking": {{ first . | toJson }}
  {{- else }}
    "tracking": null
  {{- end }}
}
{{- end -}}