{{- /* Handle non-success status codes with custom message */ -}}
{{ if ne .response.statusCode 200 }}
    {{- $statusCodeFamily := print .response.statusCode | printf "%.1s" -}}

    {{- if eq .response.statusCode 422 -}}
{
    "errors": [
        {
            "message": {{ coalesce .rawData.message .rawData.errors.message | toJson }}
        }
    ]
}
    {{ else }}
        {{- /* Capture any included errors, if there are any */ -}}
        {{ range .rawData.errors }}
            {{ printf "unable to execute action: '%s'" . | stop }}
        {{ else }}
            {{ printf "unable to execute action: '%s'" .rawData.message | stop }}
        {{ end }}
    {{ end }}
{{- /* Successful action response transformation */ -}}
{{- else -}}
{
    "url": "{{ .rawData.url }}"
}
{{- end -}}