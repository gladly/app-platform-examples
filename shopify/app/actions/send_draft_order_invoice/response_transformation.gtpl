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
        "message": {{ toJson $error.message }},
        "code": {{ if $error.extensions }}{{- toJson $error.extensions.code -}}{{ else }}null{{ end }}
        }
        {{- if lt (add $index 1) (len $errors) -}},{{- end -}}
        {{- end -}}
    ]
}
{{- else if (gt (len .rawData.data.draftOrderInvoiceSend.userErrors) 0) -}}
{
    "draftOrder": null,
    "userErrors": [
        {{- $userErrors := .rawData.data.draftOrderInvoiceSend.userErrors -}}
        {{- range $index, $error := $userErrors -}}
        {
        "message": {{ toJson $error.message }},
        "field": {{ if $error.field }}"{{ join "." $error.field }}"{{ else }}null{{ end }}
        }
        {{- if lt (add $index 1) (len $userErrors) -}},{{- end -}}
        {{- end -}}
    ]
}
{{- else -}}
{{- $draftOrder := .rawData.data.draftOrderInvoiceSend.draftOrder -}}
{
    "draftOrder": {
        "id": "{{ $draftOrder.id }}",
        "name": {{ toJson $draftOrder.name }},
        "status": {{ toJson $draftOrder.status }},
        "invoiceUrl": {{ toJson $draftOrder.invoiceUrl }},
        "invoiceSentAt": {{ toJson $draftOrder.invoiceSentAt }}
    }
}
{{- end -}}
