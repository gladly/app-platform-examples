{{- if (ne .response.statusCode 200) -}}
    {{- $error := toJson .response -}}
    {{- fail $error -}}
{{- else -}}
{{ toJson .rawData.data }}
{{- end -}}