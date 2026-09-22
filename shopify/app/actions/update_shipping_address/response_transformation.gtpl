{{- $errors := .rawData.errors -}}
{{- $hasErrors := or (and $errors (gt (len $errors) 0)) false -}}

{{ if (ne .response.statusCode 200) }}
    {{- $error := toJson .rawData.errors -}}
	{{- fail  $error -}}

{{/*
    check bad format errors (e.g. Variable $input of type OrderInput! was provided invalid value for shippingAddress.address3 (Field is not defined on MailingAddressInput))
*/}}
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
{{/*
    check data errors ([{"field":["shippingAddress","city"],"message":"Enter a city"}])
*/}}
{{- else if (gt (len .rawData.data.orderUpdate.userErrors) 0) -}}
{
    "userErrors": [
        {{- $userErrors := .rawData.data.orderUpdate.userErrors -}}
        {{- range $index, $error := $userErrors -}}
        {
        "message": "{{- $error.message -}}",
        "field": {{- toJson $error.field -}}
        }
        {{- if lt (add $index 1) (len $userErrors) -}},{{- end -}}
        {{- end -}}
    ]
}
{{- else -}}
{{- $order := .rawData.data.orderUpdate.order -}}
{{- if $order -}}
{
    "orderId": "{{ $order.id }}",
    "orderName": "{{ $order.name }}",
    "updatedAt": "{{ $order.updatedAt }}",
    "shippingAddress": {{ toJson $order.shippingAddress }}
}
{{- else -}}
{}
{{- end -}}
{{- end -}}