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
        "message": {{- toJson $error.message }},
        "code": {{- toJson $error.extensions.code -}}
        }
        {{- if lt (add $index 1) (len $errors) -}},{{- end -}}
        {{- end -}}
    ] 
}    
{{- else if and .rawData.data (gt (len .rawData.data.refundCreate.userErrors) 0) -}}
{
    "userErrors": [
        {{- $userErrors := .rawData.data.refundCreate.userErrors -}}
        {{- range $index, $userError := $userErrors -}}
        {
            "message": "{{- $userError.message -}}",
            "field": {{- toJson $userError.field -}}
        }
        {{- if lt (add $index 1) (len $userErrors) -}},{{- end -}}
        {{- end -}}
    ] 
}
{{- else -}}
{{- $refund := .rawData.data.refundCreate.refund -}}
    {{- with .rawData.data.refundCreate.refund -}}

    {{- $_ := set . "refundLineItems" .refundLineItems.nodes -}}
    {{- $_ := set . "transactions" .transactions.nodes }}
    {{- $_ := set . "orderAdjustments" .orderAdjustments.nodes -}}

    {{- toJson . -}}
    {{- end -}}
{{- end -}}

