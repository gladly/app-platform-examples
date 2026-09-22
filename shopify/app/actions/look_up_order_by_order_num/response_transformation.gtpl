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
    "order": null,
    "errors": [
        {{- range $index, $error := $errors -}}
        {
        "message": {{ toJson $error.message }},
        "code": {{ if $error.extensions }}{{ toJson $error.extensions.code }}{{ else }}null{{ end }}
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
