{{- $errors := .rawData.errors -}}
{{- $hasErrors := or (and $errors (gt (len $errors) 0)) false -}}

{{ if (ne .response.statusCode 200) }}
    {{- $error := toJson .rawData.errors -}}
    {{- fail $error -}}
{{- else if $hasErrors -}}
{
    "errors": [
        {{- range $index, $error := $errors -}}
        {
        "message": {{ toJson $error.message }},
        "code": {{- toJson $error.extensions.code -}}
        }
        {{- if lt (add $index 1) (len $errors) -}},{{- end -}}
        {{- end -}}
    ]
}
{{- else if (gt (len .rawData.data.orderUpdate.userErrors) 0) -}}
{
    "userErrors": [
        {{- $userErrors := .rawData.data.orderUpdate.userErrors -}}
        {{- range $index, $error := $userErrors -}}
        {
        "message": {{ toJson $error.message }},
        "field": {{- toJson $error.field -}}
        }
        {{- if lt (add $index 1) (len $userErrors) -}},{{- end -}}
        {{- end -}}
    ]
}
{{- else -}}
{
    "order": {{ toJson .rawData.data.orderUpdate.order }}
}
{{- end -}}
