{{- $errors := .rawData.errors -}}
{{- $hasErrors := or (and $errors (gt (len $errors) 0)) false -}}

{{ if (ne .response.statusCode 200) }}
    {{- $error := toJson .rawData.errors -}}
	{{- fail  $error -}}
{{- else if $hasErrors -}}
{
    "errors": [
        {{- range $index, $error := $errors -}}
        {
        "message": "{{- $error.message -}}",
        "code": {{- toJson $error.extensions.code -}}
        }
        {{- if lt (add $index 1) (len $errors) -}},{{- end -}}
        {{- end -}}
    ]
}
{{- else -}}
{{- with .rawData.data.job }}
    {{- if and (ne .query nil) (ne .query.order nil) -}}
    {{- $_ := set . "order" .query.order }}
    {{- $_ := unset . "query" }}
    {{- end}}
    {{- toJson . -}}
{{- end }}
{{- end -}}