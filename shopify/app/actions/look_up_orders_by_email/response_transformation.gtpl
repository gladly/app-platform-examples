{{/* handle errors https://shopify.dev/docs/api/admin-graphql#status_and_error_codes */}}
{{- if ne .response.statusCode 200 -}}
    {{- $statusCodeFamily := print .response.statusCode | printf "%.1s" -}}

    {{- if eq $statusCodeFamily "5" -}}
        {{- fail (toJson .rawData.errors) -}}
    {{- end -}}

    {{- if or (eq .response.statusCode 402) (eq .response.statusCode 403) (eq .response.statusCode 404) (eq .response.statusCode 423) -}}
        {{- fail (toJson .rawData.errors) -}}
    {{- end -}}

    {{- range $k, $v := .rawData.errors -}}
        {{- printf "%s: %s" (toJson $v) (toJson $k) | fail -}}
    {{- end -}}
{{- else -}}
{
    "errors": [
        {{- range $idx, $err := $.rawData.errors }}
        {
            "message": {{ toJson $err.message }}
            {{- if $err.extensions -}}
            ,
            "code": {{ if $err.extensions }}{{ toJson $err.extensions.code }}{{ else }}null{{ end }}
            {{- end }}
        }
        {{- if lt (add $idx 1) (len $.rawData.errors) }},{{ end -}}
        {{- end }}
    ],
    "orders": [
        {{- if .rawData.data }}
        {{- $orders := .rawData.data.orders.nodes }}
        {{- range $index, $order := $orders -}}
        {{- template "orderTemplate" $order }}
        {{- if lt (add $index 1) (len $orders) -}},{{- end -}}
        {{- end }}
        {{- end }}
    ]
}
{{- end -}}


{{- define "orderTemplate" }}
    {{- $order := . -}}

    {{- /* flatten connection wrappers */ -}}
    {{- $_ := set $order "shippingLines" $order.shippingLines.nodes -}}
    {{- $_ := set $order "lineItems" $order.lineItems.nodes -}}

    {{- /* rename Shopify fields to the slim Order shape */ -}}
    {{- $_ := set $order "orderNumber" $order.name -}}
    {{- $_ := set $order "fulfillmentStatus" $order.displayFulfillmentStatus -}}
    {{- $_ := unset $order "displayFulfillmentStatus" -}}
    {{- $_ := set $order "financialStatus" $order.displayFinancialStatus -}}
    {{- $_ := unset $order "displayFinancialStatus" -}}
    {{- $_ := set $order "orderStatusUrl" $order.statusPageUrl -}}
    {{- $_ := unset $order "statusPageUrl" -}}

    {{- /* A1: keep EVERY line item (custom lines have nil product/variant — do not drop them) */ -}}
    {{- $lines := list -}}
    {{- range $order.lineItems -}}
        {{- $line := . -}}
        {{- /* A1 pricing (P1 deferred): price <- originalUnitPriceSet.shopMoney (Money), totalDiscount <- totalDiscountSet.shopMoney.amount (String) */ -}}
        {{- if $line.originalUnitPriceSet -}}
            {{- $_ := set $line "price" $line.originalUnitPriceSet.shopMoney -}}
        {{- end -}}
        {{- if $line.totalDiscountSet -}}
            {{- $_ := set $line "totalDiscount" $line.totalDiscountSet.shopMoney.amount -}}
        {{- end -}}
        {{- /* A6: giftCard <- Shopify isGiftCard */ -}}
        {{- $_ := set $line "giftCard" $line.isGiftCard -}}
        {{- /* drop the raw source fields not in the slim LineItem schema */ -}}
        {{- $_ := unset $line "originalUnitPriceSet" -}}
        {{- $_ := unset $line "totalDiscountSet" -}}
        {{- $_ := unset $line "isGiftCard" -}}
        {{- $lines = append $lines $line -}}
    {{- end -}}
    {{- $_ := set $order "lineItems" $lines -}}

    {{- toJson $order -}}
{{- end -}}
