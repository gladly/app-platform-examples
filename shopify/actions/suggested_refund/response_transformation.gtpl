{{- $errors := .rawData.errors -}}
{{- $hasErrors := or (and $errors (gt (len $errors) 0)) false -}}

{{ if (ne .response.statusCode 200) }}
    {{- $error := toJson .rawData.errors -}}
	{{- print $error | fail -}}
{{- else if $hasErrors -}}
{
    {{- $error := (index .rawData.errors 0).message }}
    "order": null,
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

{{ toJson .rawData.data.order }}

{{- end -}}