{{if eq .response.statusCode 200}}
    {{- if .inputs.enabledOnly}}
        {{- $enabledInboxes := 0}}
        [
        {{- range .rawData}}
            {{- if not .disabled}}
                {{- if gt $enabledInboxes 0}},{{end}}
                {{toJson .}}
                {{- $enabledInboxes = add1 $enabledInboxes}}
            {{- end}}
        {{- end}}
        ]
    {{- else}}
        {{toJson .rawData}}
    {{- end}}
{{- else}}
    {{- printf "inbox request failed with status code: %d" .response.statusCode | fail}}
{{- end}}