{{- if (ne .response.statusCode 200) -}}
    {{- toJson .rawData -}}
{{- else if (.rawData.error)}}
{{- toJson .rawData -}}
{{- else -}}
{ "subscription": {{- toJson .rawData -}} }
{{- end -}}