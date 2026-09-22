{{if eq .response.statusCode 200}}
    {{- toJson .rawData}}
{{- else}}
    {{- printf "agent list request failed with status code: %d" .response.statusCode | fail}}
{{- end}}
