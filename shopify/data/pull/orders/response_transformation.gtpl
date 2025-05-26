{{- $errors := .rawData.errors -}}
    
{{- if and (eq .response.statusCode 200) .rawData.errors -}}
    {{/* make single string from errors. format: <field> - <message>; ... */}}
    {{- $errorMessages := "" -}}
    {{- range $index, $error := $errors -}}
        {{- $field := $error.message -}}
        {{- if $index -}}
            {{- $errorMessages = (print $errorMessages "; " $field " - " $error.extensions.code) -}}
        {{- else -}}
            {{- $errorMessages = (print $field " - " $error.extensions.code) -}}
        {{- end -}}
    {{- end -}}

    {{- fail $errorMessages -}}
{{- else -}}
{{/* Iterate through all customer orders. */}}
[   
    {{- $orders := .rawData.data.orders.nodes }}
    {{- range $index, $order := $orders -}}

    {{- template "orderTemplate" $order }}

    {{- if lt (add $index 1) (len $orders) -}},{{- end -}}
    {{- end }}
]
{{- end -}}

{{- define "orderTemplate" }}
    {{- $order := . -}}
    {{- $_ := set $order "customerId" $order.customer.id -}}
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