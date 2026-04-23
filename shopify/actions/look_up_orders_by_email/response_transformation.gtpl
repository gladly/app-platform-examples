{{/* handle errors https://shopify.dev/docs/api/admin-graphql#status_and_error_codes */}}
{{- if ne .response.statusCode 200 -}}
    {{ $statusCodeFamily := print .response.statusCode | printf "%.1s" }}

    {{ if eq $statusCodeFamily "5" }}
        {{ fail .rawData.errors }}
    {{ end }}

    {{ if or (eq .response.statusCode 402) (eq .response.statusCode 403) (eq .response.statusCode 404) (eq .response.statusCode 423) }}
        {{ fail .rawData.errors }}
    {{ end }}

    {{ range $k, $v := .rawData.errors }}
        {{  printf "%s: '%s'" $v $k | fail }}
    {{ end }}
{{- else -}}
{
    "errors": [
        {{- range $idx, $err := $.rawData.errors }}
        {
            "message": "{{ $err.message }}"
            {{- if $err.extensions.code -}}
            ,
            "code": "{{ $err.extensions.code }}"
            {{- end }}
        }
        {{- if lt (add $idx 1) (len $.rawData.errors) }},{{ end -}}
        {{- end }}
    ],
    "orders": [
        {{ if .rawData.data }}
        {{- $orders := .rawData.data.orders.nodes }}
        {{ with .rawData.data }}
        {{- range $index, $order := .orders.nodes -}}


        {{- template "orderTemplate" $order }}

        {{- if lt (add $index 1) (len $orders) -}},{{- end -}}
        {{- end }}
        {{- end }}
        {{- end }}
    ]
}
{{- end -}}


{{- define "orderTemplate" }}
    {{- $order := . -}}
    {{- $_ := set $order "shippingLines" $order.shippingLines.nodes -}}
    {{- $_ := set $order "orderNumber" $order.name -}}
    
    {{- /* get rid of order->fulfillments[<i>]->fulfillmentLineItems->"nodes" and handle trackingInfo.url, trackingInfo.company, and trackingInfo.number nil values */ -}}
    {{- $arr := list -}}
    {{- range $order.fulfillments -}}
        {{- $_ := set . "fulfillmentLineItems" .fulfillmentLineItems.nodes -}}
        {{- /* handle trackingInfo nil values */ -}}
        {{- $trackingArr := list -}}
        {{- range .trackingInfo -}}
            {{- if eq .url nil -}}
                {{- $_ := set . "url" "" -}}
            {{- end -}}
            {{- if eq .company nil -}}
                {{- $_ := set . "company" "" -}}
            {{- end -}}
            {{- if eq .number nil -}}
                {{- $_ := set . "number" "" -}}
            {{- end -}}
            {{- $trackingArr = append $trackingArr . -}}
        {{- end -}}
        {{- $_ := set . "trackingInfo" $trackingArr -}}
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