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
        "field": "{{- $error.field -}}"
        }
        {{- if lt (add $index 1) (len $userErrors) -}},{{- end -}}
        {{- end -}}
    ]
}
{{- else -}}
{
    "order": {{- template "orderTemplate" .rawData.data.orderUpdate }}
}
{{- end -}}


{{- define "orderTemplate" }}
    {{- $order := . -}}
    {{- $_ := set $order "shippingLines" $order.shippingLines.nodes -}}
    
    {{- /* get rid of order->fulfillments[<i>]->fulfillmentLineItems->"nodes" */ -}}
    {{- $arr := list -}}
    {{- range $order.fulfillments -}}
        {{- $_ := set . "fulfillmentLineItems" .fulfillmentLineItems.nodes -}}
        {{- $arr = append $arr . -}}
    {{- end -}}
    {{- $_ := set $order "fulfillments" $arr -}}
    
    {{- /* get rid of order->lineItems[<i>]->"nodes" */ -}}
    {{- $_ := set $order "lineItems" $order.lineItems.nodes -}}
    
    {{/* get rid of order->lineItems[<i>]->product->variants->"nodes" */ -}}
    {{- $arr := list -}}
    {{- range $order.lineItems -}}
        {{- if ne .product nil }}
            {{- $_ := set .product "variants" .product.variants.nodes -}}
            {{- $arr = append $arr . -}}
        {{- end -}}
    {{- end -}}
    {{- $_ := set $order "lineItems" $arr -}}
    {{- toJson $order -}}
{{- end -}}