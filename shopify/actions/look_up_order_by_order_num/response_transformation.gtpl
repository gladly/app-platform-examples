{{/* Error handling:
    - Non-200 responses will result in 500 error
    - Any Shopify errors will be returned
*/}}

{{- $errors := .rawData.errors -}}
{{- $hasErrors := or (and $errors (gt (len $errors) 0)) false -}}

{{/* 
    Check if no order is found
    - This check verifies if the response data contains no order records.
*/}}

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
{{- else if and .rawData.data (eq (len .rawData.data.orders.nodes) 0) -}}
{
     "order": null,
     "errors": [{
        "message": "Order is not found",
        "code": "not_found"
     }]
}
{{- else -}}
{
    "order": {{- template "orderTemplate" (index .rawData.data.orders.nodes 0) -}}
}
{{- end -}}

{{- define "orderTemplate" }}
    {{- $order := . -}}
    {{- $_ := set $order "shippingLines" $order.shippingLines.nodes -}}
    {{- $_ := set $order "orderNumber" $order.name -}}
    
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
