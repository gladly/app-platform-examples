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
        "message": {{ if $error.message }}"{{ $error.message }}"{{ else }}null{{ end }},
        "code": {{- toJson $error.extensions.code -}}
        }
        {{- if lt (add $index 1) (len $errors) -}},{{- end -}}
        {{- end -}}
    ]
}
{{- else if (gt (len .rawData.data.orderCancel.orderCancelUserErrors) 0) -}}
{
    "jobId": null,
    "userErrors": [
        {{- $userErrors := .rawData.data.orderCancel.orderCancelUserErrors -}}
        {{- range $index, $error := $userErrors -}}
        {
        "message": "{{- $error.message -}}",
        "code": "{{- $error.code -}}",
        "field": "{{- $error.field -}}"
        }
        {{- if lt (add $index 1) (len $userErrors) -}},{{- end -}}
        {{- end -}}
    ]
}
{{- else -}}
{
   "jobId": "{{ .rawData.data.orderCancel.job.id }}",
   "done": {{ .rawData.data.orderCancel.job.done }}
}
{{- end -}}