{{- /* order_by_identifier — map Shopify's orderByIdentifier(identifier:{ id }) onto the
       OrderDetail schema. We BUILD FRESH dicts (rather than mutate the raw object) so the
       emitted shape matches OrderDetail exactly: connections flattened to plain lists,
       the *Set payment rollups renamed (totalReceivedSet -> totalReceived, etc.), and
       each sibling-block line keyed back to its order line by `lineItemId`.
       Ground-truthed against partner-ela at API 2026-07 (2026-08).

       Optional fields are added with a guarded `set` (never a bare `nil` literal — Go
       templates reject `nil` in command position), so an absent field is simply omitted
       and projects to null.

       Error / not-found contract (mirrors the schema docstring): non-200 -> fail; GraphQL
       errors -> { order: null, errors: [...] }; GID resolves to no order -> { order: null,
       errors: [{ message: "Order is not found", code: "not_found" }] }. */ -}}

{{- $errors := .rawData.errors -}}
{{- $hasErrors := and $errors (gt (len $errors) 0) -}}

{{ if (ne .response.statusCode 200) -}}
    {{- print (toJson .rawData.errors) | fail -}}
{{- else if $hasErrors -}}
{
    "order": null,
    "errors": [
        {{- range $i, $e := $errors -}}
        {{- if $i }},{{ end }}
        { "message": {{ toJson $e.message }}, "code": {{ toJson $e.extensions.code }} }
        {{- end }}
    ]
}
{{- else if eq .rawData.data.orderByIdentifier nil -}}
{
    "order": null,
    "errors": [{ "message": "Order is not found", "code": "not_found" }]
}
{{- else -}}
{{- $o := .rawData.data.orderByIdentifier -}}

{{- /* line items: flatten the connection; rename isGiftCard -> giftCard; derive
       price (Money) / totalDiscount (string) from their *Set fields when present. */ -}}
{{- $lineItems := list -}}
{{- range $o.lineItems.nodes -}}
  {{- $li := dict
      "id" .id "name" .name "title" .title "sku" .sku "vendor" .vendor
      "giftCard" .isGiftCard
      "quantity" .quantity "currentQuantity" .currentQuantity "unfulfilledQuantity" .unfulfilledQuantity -}}
  {{- /* variant: raw passthrough — { id, title, selectedOptions { name value } } already match the
         schema. Null for custom lines & deleted-variant catalog lines (guard so nothing is emitted). */ -}}
  {{- if .variant -}}{{- $_ := set $li "variant" .variant -}}{{- end -}}
  {{- /* product: flatten collections connection -> collections (CollectionRef list) and set
         hasMoreCollections from collections.pageInfo.hasNextPage. Null on custom lines. */ -}}
  {{- if .product -}}
    {{- $prod := dict "id" .product.id "title" .product.title "vendor" .product.vendor "productType" .product.productType -}}
    {{- if .product.collections -}}
      {{- $_ := set $prod "collections" .product.collections.nodes -}}
      {{- if .product.collections.pageInfo -}}{{- $_ := set $prod "hasMoreCollections" .product.collections.pageInfo.hasNextPage -}}{{- end -}}
    {{- end -}}
    {{- $_ := set $li "product" $prod -}}
  {{- end -}}
  {{- if .originalUnitPriceSet -}}{{- $_ := set $li "price" .originalUnitPriceSet.shopMoney -}}{{- end -}}
  {{- if .totalDiscountSet -}}{{- $_ := set $li "totalDiscount" .totalDiscountSet.shopMoney.amount -}}{{- end -}}
  {{- /* customAttributes: raw passthrough — { key, value } already matches the schema.
         Shopify returns [] for a line with no attributes, which is most of them, so
         omit the key entirely rather than emitting an empty list on every line. */ -}}
  {{- if .customAttributes -}}{{- $_ := set $li "customAttributes" .customAttributes -}}{{- end -}}
  {{- $lineItems = append $lineItems $li -}}
{{- end -}}

{{- /* fulfillments: PLAIN LIST on the live API; flatten fulfillmentLineItems to
       { quantity, lineItemId }. */ -}}
{{- $fulfillments := list -}}
{{- range $o.fulfillments -}}
  {{- $flis := list -}}
  {{- range .fulfillmentLineItems.nodes -}}
    {{- $fli := dict "quantity" .quantity -}}
    {{- if .lineItem -}}{{- $_ := set $fli "lineItemId" .lineItem.id -}}{{- end -}}
    {{- $flis = append $flis $fli -}}
  {{- end -}}
  {{- $fulfillments = append $fulfillments (dict
      "id" .id "name" .name "displayStatus" .displayStatus "deliveredAt" .deliveredAt
      "estimatedDeliveryAt" .estimatedDeliveryAt
      "trackingInfo" .trackingInfo "lineItems" $flis) -}}
{{- end -}}

{{- /* fulfillmentOrders: connection; assignedLocation.name -> String; the live field is
       `fulfillmentHolds`, the schema calls it `holds`. */ -}}
{{- $fulfillmentOrders := list -}}
{{- range $o.fulfillmentOrders.nodes -}}
  {{- $foLines := list -}}
  {{- range .lineItems.nodes -}}
    {{- $fol := dict "remainingQuantity" .remainingQuantity -}}
    {{- if .lineItem -}}{{- $_ := set $fol "lineItemId" .lineItem.id -}}{{- end -}}
    {{- $foLines = append $foLines $fol -}}
  {{- end -}}
  {{- $fo := dict "id" .id "status" .status "holds" .fulfillmentHolds "lineItems" $foLines -}}
  {{- if .assignedLocation -}}{{- $_ := set $fo "assignedLocation" .assignedLocation.name -}}{{- end -}}
  {{- /* deliveryMethod: ship-vs-pickup type + estimated window. min/max are null for
         admin/API/manual/pickup orders (ground-truth #1071) -> pass through as null. */ -}}
  {{- if .deliveryMethod -}}{{- $_ := set $fo "deliveryMethod" (dict "methodType" .deliveryMethod.methodType "minDeliveryDateTime" .deliveryMethod.minDeliveryDateTime "maxDeliveryDateTime" .deliveryMethod.maxDeliveryDateTime) -}}{{- end -}}
  {{- $fulfillmentOrders = append $fulfillmentOrders $fo -}}
{{- end -}}

{{- /* returns: connection; reason from returnReasonDefinition.name (human-readable, per
       schema — NOT the deprecated returnReason enum); reasonNote from returnReasonNote;
       lineItemId via fulfillmentLineItem.lineItem.id. */ -}}
{{- $returns := list -}}
{{- range $o.returns.nodes -}}
  {{- $rLines := list -}}
  {{- range .returnLineItems.nodes -}}
    {{- $rl := dict "quantity" .quantity "reasonNote" .returnReasonNote -}}
    {{- if .returnReasonDefinition -}}{{- $_ := set $rl "reason" .returnReasonDefinition.name -}}{{- end -}}
    {{- if .fulfillmentLineItem -}}{{- if .fulfillmentLineItem.lineItem -}}{{- $_ := set $rl "lineItemId" .fulfillmentLineItem.lineItem.id -}}{{- end -}}{{- end -}}
    {{- $rLines = append $rLines $rl -}}
  {{- end -}}
  {{- $returns = append $returns (dict
      "id" .id "name" .name "status" .status "totalQuantity" .totalQuantity "lineItems" $rLines) -}}
{{- end -}}

{{- /* refunds: PLAIN LIST on the live API. Build the RefundOverview shape: keep note as `note`,
       totalRefundedSet as-is; flatten refundLineItems to { quantity, restockType, lineItemId }
       and the transactions connection (the money movements) to RefundOrderTransaction. */ -}}
{{- $refunds := list -}}
{{- range $o.refunds -}}
  {{- $rfLines := list -}}
  {{- range .refundLineItems.nodes -}}
    {{- $rfl := dict "quantity" .quantity "subtotalSet" .subtotalSet "totalTaxSet" .totalTaxSet "restockType" .restockType -}}
    {{- if .lineItem -}}{{- $_ := set $rfl "lineItemId" .lineItem.id -}}{{- end -}}
    {{- $rfLines = append $rfLines $rfl -}}
  {{- end -}}
  {{- $rfTxns := list -}}
  {{- range .transactions.nodes -}}
    {{- $rfTxns = append $rfTxns (dict
        "id" .id "kind" .kind "status" .status "gateway" .gateway
        "formattedGateway" .formattedGateway "accountNumber" .accountNumber "amountSet" .amountSet) -}}
  {{- end -}}
  {{- $refunds = append $refunds (dict
      "id" .id "createdAt" .createdAt "note" .note "totalRefundedSet" .totalRefundedSet
      "lineItems" $rfLines "transactions" $rfTxns) -}}
{{- end -}}

{{- $order := dict
    "id" $o.id "name" $o.name "confirmationNumber" $o.confirmationNumber
    "displayFinancialStatus" $o.displayFinancialStatus "displayFulfillmentStatus" $o.displayFulfillmentStatus
    "createdAt" $o.createdAt "processedAt" $o.processedAt
    "email" $o.email "note" $o.note "tags" $o.tags
    "cancelReason" $o.cancelReason "cancelledAt" $o.cancelledAt
    "originalTotalPriceSet" $o.originalTotalPriceSet "originalTotalDutiesSet" $o.originalTotalDutiesSet
    "subtotalPriceSet" $o.subtotalPriceSet
    "totalShippingPriceSet" $o.totalShippingPriceSet "totalTaxSet" $o.totalTaxSet
    "totalPriceSet" $o.totalPriceSet
    "currentSubtotalPriceSet" $o.currentSubtotalPriceSet "currentTotalDiscountsSet" $o.currentTotalDiscountsSet
    "currentCartDiscountAmountSet" $o.currentCartDiscountAmountSet
    "currentShippingPriceSet" $o.currentShippingPriceSet "currentTotalTaxSet" $o.currentTotalTaxSet
    "currentTotalDutiesSet" $o.currentTotalDutiesSet
    "currentTotalPriceSet" $o.currentTotalPriceSet
    "totalReceived" $o.totalReceivedSet "totalRefunded" $o.totalRefundedSet
    "totalRefundedShippingSet" $o.totalRefundedShippingSet
    "totalOutstanding" $o.totalOutstandingSet
    "refundDiscrepancySet" $o.refundDiscrepancySet "totalCapturableSet" $o.totalCapturableSet
    "shippingAddress" $o.shippingAddress "billingAddress" $o.billingAddress
    "lineItems" $lineItems "fulfillments" $fulfillments "fulfillmentOrders" $fulfillmentOrders
    "returns" $returns "refunds" $refunds
    "additionalFees" $o.additionalFees -}}

{{- /* Truncation flags for the cursor-paginated connections (pageInfo.hasNextPage), default false.
       fulfillments and refunds are queried WITHOUT `first:` (they return the full list), so they
       need no flag. */ -}}
{{- $hmli := false -}}{{- if $o.lineItems -}}{{- if $o.lineItems.pageInfo -}}{{- $hmli = $o.lineItems.pageInfo.hasNextPage -}}{{- end -}}{{- end -}}
{{- $_ := set $order "hasMoreLineItems" $hmli -}}
{{- $hmfo := false -}}{{- if $o.fulfillmentOrders -}}{{- if $o.fulfillmentOrders.pageInfo -}}{{- $hmfo = $o.fulfillmentOrders.pageInfo.hasNextPage -}}{{- end -}}{{- end -}}
{{- $_ := set $order "hasMoreFulfillmentOrders" $hmfo -}}
{{- $hmr := false -}}{{- if $o.returns -}}{{- if $o.returns.pageInfo -}}{{- $hmr = $o.returns.pageInfo.hasNextPage -}}{{- end -}}{{- end -}}
{{- $_ := set $order "hasMoreReturns" $hmr -}}

{{- /* Order-origin & delivery signals. salesChannelName from
       channelInformation.channelDefinition.channelName (null when app/admin/draft-created
       -> key omitted, ground-truth #1071/#1068); sourceAppName from app.name; deliveryMethodName
       from the FIRST shipping line's title (empty list -> omitted, ground-truth #1068). */ -}}
{{- if $o.channelInformation -}}{{- if $o.channelInformation.channelDefinition -}}{{- $_ := set $order "salesChannelName" $o.channelInformation.channelDefinition.channelName -}}{{- end -}}{{- end -}}
{{- if $o.app -}}{{- $_ := set $order "sourceAppName" $o.app.name -}}{{- end -}}
{{- if $o.shippingLines -}}{{- range $i, $sl := $o.shippingLines.nodes -}}{{- if eq $i 0 -}}{{- $_ := set $order "deliveryMethodName" $sl.title -}}{{- end -}}{{- end -}}{{- end -}}

{{- /* purchasingEntity: resolve the union by __typename (mirrors create_draft_order_from_order).
       Customer -> flat CustomerSummary: defaultEmailAddress/defaultPhoneNumber/defaultAddress map to
       email/phone/defaultAddressFormatted, each null-guarded (ground-truth #1068 had defaultPhoneNumber
       null). PurchasingCompany -> PurchasingCompanyRef. A guest / unattributed order has
       purchasingEntity null (ground-truth #1063) -> the key is simply omitted (projects to null). */ -}}
{{- if $o.purchasingEntity -}}
  {{- $pe := $o.purchasingEntity -}}
  {{- if eq $pe.__typename "Customer" -}}
    {{- $customer := dict "id" $pe.id "displayName" $pe.displayName -}}
    {{- if $pe.defaultEmailAddress -}}{{- $_ := set $customer "email" $pe.defaultEmailAddress.emailAddress -}}{{- end -}}
    {{- if $pe.defaultPhoneNumber -}}{{- $_ := set $customer "phone" $pe.defaultPhoneNumber.phoneNumber -}}{{- end -}}
    {{- if $pe.defaultAddress -}}{{- $_ := set $customer "defaultAddressFormatted" $pe.defaultAddress.formatted -}}{{- end -}}
    {{- $_ := set $order "purchasingEntity" (dict "customer" $customer) -}}
  {{- else if eq $pe.__typename "PurchasingCompany" -}}
    {{- $company := dict "company" $pe.company "location" $pe.location -}}
    {{- if $pe.contact -}}
      {{- $contact := dict "id" $pe.contact.id -}}
      {{- if $pe.contact.customer -}}
        {{- $cc := $pe.contact.customer -}}
        {{- $_ := set $contact "customer" (dict "id" $cc.id "displayName" $cc.displayName) -}}
      {{- end -}}
      {{- $_ := set $company "contact" $contact -}}
    {{- end -}}
    {{- $_ := set $order "purchasingEntity" (dict "company" $company) -}}
  {{- end -}}
{{- end -}}

{
    "order": {{ toJson $order }},
    "errors": []
}
{{- end -}}