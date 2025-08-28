{{- /* Handle non-success status codes with custom message */ -}}
{{- if not ( list 200 201 | has .response.statusCode ) }}
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
        {{ range .rawData.errors }}
            {{ printf "unable to execute action: '%s'" . | fail }}
        {{ else }}
            {{ printf "unable to execute action: '%s'" .rawData.message | fail }}
        {{ end }}
    {{ end }}
{{- else -}}
    {{- /* Successful action response transformation */ -}}
    {{ toJson ( get .rawData "id" | int64 | toString | set .rawData "id" ) }}
{{- end -}}