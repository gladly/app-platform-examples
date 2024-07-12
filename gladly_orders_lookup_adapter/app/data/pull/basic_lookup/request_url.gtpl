{{if or (not .integration.configuration.autoLinkFields) (eq (len .integration.configuration.autoLinkFields) 0)}}
    {{- stop "custom lookup adaptor is not configured for auto linking"}}
{{- end}}
{{- .integration.configuration.baseUrl}}