{{/* handle errors https://shopify.dev/docs/api/admin-graphql#status_and_error_codes */}}
{{- if and (eq .response.statusCode 200) .rawData.errors -}}
    {{/* make single string from errors. format: <message> - <code>; ... */}}
    {{- $errorMessages := "" -}}
    {{- range $index, $error := .rawData.errors -}}
        {{- $code := "" -}}
        {{- if $error.extensions -}}{{- $code = $error.extensions.code -}}{{- end -}}
        {{- if $index -}}
            {{- $errorMessages = (print $errorMessages "; " $error.message " - " $code) -}}
        {{- else -}}
            {{- $errorMessages = (print $error.message " - " $code) -}}
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

    {{- /* customerId feeds the @parentId on metafields and the data-pull external id */ -}}
    {{- $_ := set $order "customerId" $order.customer.id -}}

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

    {{- /* The metafields data pull caps each owner's metafields, so a short list
           is indistinguishable from a complete one. Carry Shopify's hasNextPage
           for this order's metafields and drop the connection wrapper: the
           metafields themselves come from the metafields data pull. */ -}}
    {{- if $order.metafields -}}
        {{- $_ := set $order "hasMoreMetafields" $order.metafields.pageInfo.hasNextPage -}}
    {{- else -}}
        {{- $_ := set $order "hasMoreMetafields" nil -}}
    {{- end -}}
    {{- $_ := unset $order "metafields" -}}

    {{- toJson $order -}}
{{- end -}}
