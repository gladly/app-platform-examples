{{- if not .rawData.items -}}
[]
{{- else -}}
{{/* Map Magento's raw order statuses to normalized Gladly statuses for order items.
     Magento allows custom statuses, so the order-level status is passed through as-is,
     but item-level status uses this normalized set for consistent display in Gladly. */}}
{{- $statusMap := dict
    "complete" "fulfilled"
    "completed" "fulfilled"
    "processing" "processing"
    "pending" "pending"
    "canceled" "cancelled"
    "cancelled" "cancelled"
    "closed" "fulfilled"
    "holded" "on_hold"
-}}
{{- $results := list -}}
{{- range .rawData.items -}}
    {{/* Map Magento order status to Gladly's normalized item status values. Falls back to "pending" for any unknown/custom statuses */}}
    {{- $mappedStatus := default "pending" (get $statusMap .status) -}}
    {{- $currencyCode := .base_currency_code -}}
    {{- $order := dict
        "orderId" (toString (int64 .entity_id))
        "incrementId" .increment_id
        "createdAt" (dateInZone "2006-01-02T15:04:05Z" (printf "%s UTC" .created_at | toDate "2006-01-02 15:04:05 MST") "UTC")
        "updatedAt" (dateInZone "2006-01-02T15:04:05Z" (printf "%s UTC" .updated_at | toDate "2006-01-02 15:04:05 MST") "UTC")
        "status" .status
        "grandTotal" (printf "%.2f" .grand_total)
        "discountAmount" (printf "%.2f" .discount_amount)
        "taxAmount" (printf "%.2f" .tax_amount)
        "shippingAmount" (printf "%.2f" .shipping_amount)
        "currencyCode" $currencyCode
        "shippingMethod" .shipping_description
        "customerEmail" .customer_email
        "orderLink" (printf "%s/sales/order/view/order_id/%v" $.integration.configuration.magento_deep_link_url (int64 .entity_id))
    -}}
    {{- if .customer_id -}}
        {{- $_ := set $order "customerId" (toString (int64 .customer_id)) -}}
    {{- end -}}
    {{- if .payment -}}
        {{- $_ := set $order "paymentMethod" .payment.method -}}
    {{- end -}}
    {{- if .coupon_code -}}
        {{- $_ := set $order "couponCode" .coupon_code -}}
    {{- end -}}
    {{/* Build order items */}}
    {{- $orderItems := list -}}
    {{- range .items -}}
        {{- $item := dict
            "itemId" (toString (int64 .item_id))
            "productId" (toString (int64 .product_id))
            "name" .name
            "sku" .sku
            "status" $mappedStatus
            "unitPrice" (printf "%.2f" .price)
            "quantity" (printf "%v" .qty_ordered)
            "currencyCode" $currencyCode
            "productType" .product_type
            "rowTotal" (printf "%.2f" .row_total)
            "taxAmount" (printf "%.2f" .tax_amount)
        -}}
        {{- if .weight -}}
            {{- $_ := set $item "weight" .weight -}}
        {{- end -}}
        {{- if and .discount_amount (ne (printf "%.2f" .discount_amount) "0.00") -}}
            {{- $_ := set $item "discountAmount" (printf "%.2f" .discount_amount) -}}
        {{- end -}}
        {{- $_ := set $item "qtyShipped" .qty_shipped -}}
        {{- $_ := set $item "qtyInvoiced" .qty_invoiced -}}
        {{- $_ := set $item "qtyCanceled" .qty_canceled -}}
        {{- $orderItems = append $orderItems $item -}}
    {{- end -}}
    {{- $_ := set $order "items" $orderItems -}}
    {{/* Build billing address */}}
    {{- if .billing_address -}}
        {{- $addr := dict -}}
        {{/* Magento provides firstname and lastname separately; combined here because BillingAddress.name is a single field */}}
        {{- $name := printf "%s %s" (default "" .billing_address.firstname) (default "" .billing_address.lastname) | trim -}}
        {{- if $name -}}
            {{- $_ := set $addr "name" $name -}}
        {{- end -}}
        {{- if .billing_address.street -}}
            {{- $_ := set $addr "street1" (first .billing_address.street) -}}
            {{- if gt (len .billing_address.street) 1 -}}
                {{- $_ := set $addr "street2" (index .billing_address.street 1) -}}
            {{- end -}}
        {{- end -}}
        {{- if .billing_address.city -}}
            {{- $_ := set $addr "city" .billing_address.city -}}
        {{- end -}}
        {{- if .billing_address.region -}}
            {{- $_ := set $addr "state" .billing_address.region -}}
        {{- end -}}
        {{- if .billing_address.postcode -}}
            {{- $_ := set $addr "zip" .billing_address.postcode -}}
        {{- end -}}
        {{- if .billing_address.country_id -}}
            {{- $_ := set $addr "country" .billing_address.country_id -}}
        {{- end -}}
        {{- if .billing_address.telephone -}}
            {{- $_ := set $addr "phone" .billing_address.telephone -}}
        {{- end -}}
        {{- $_ := set $order "billingAddress" $addr -}}
    {{- end -}}
    {{- $results = append $results $order -}}
{{- end -}}
{{- toJson $results -}}
{{- end -}}