
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
{{- else if and .rawData.data (eq (len .rawData.data.orders.edges) 0) -}}
{
     "order": null,
     "errors": [{
        "message": "Order is not found",
        "code": "not_found"
     }]
}
{{- else -}}
{
    "order": {
        {{- $orderEdge := (index .rawData.data.orders.edges 0) -}}
        {{- $order := $orderEdge.node}}

        {{- template "orderFulfillments" $order -}},

        "order_status_url": "{{ $order.statusPageUrl }}",
        "billing_address": {{ toJson $order.billingAddress }},
        "currency": "{{ $order.currencyCode }}",
        "current_subtotal_price": "{{ $order.currentSubtotalPriceSet.presentmentMoney.amount }}",
        "current_total_discounts": "{{ $order.currentTotalDiscountsSet.presentmentMoney.amount }}",
        "current_total_price": "{{ $order.currentTotalPriceSet.presentmentMoney.amount }}",
        "current_total_tax": "{{ $order.currentTotalTaxSet.presentmentMoney.amount }}",
        "email": {{ toJson $order.email }},
        "financial_status": "{{ $order.displayFinancialStatus }}",
        "fulfillment_status": "{{ $order.displayFulfillmentStatus }}",
        "id": "{{ $order.id }}",
        "name": {{ toJson $order.name }},
        "order_number": {{ toJson $order.name }},
        "shipping_address": {{ toJson $order.shipmentAddress }},
        "cancel_reason": {{ toJson $order.cancelReason }},
        "note": {{ toJson $order.note }},
        "tags": {{ toJson $order.tags }},
        "created_at": {{ toJson $order.createdAt }},
        "updated_at": {{ toJson $order.updatedAt }},
        "line_items": [
            {{- range $index, $lineItemsEdge := $order.lineItems.edges -}}
            {{- $lineItem := $lineItemsEdge.node}}
            {
                "id": "{{ $lineItem.id }}",
                "name": "{{ $lineItem.name }}",
                "price": "{{ $lineItem.originalUnitPriceSet.presentmentMoney.amount }}",
                {{- $product := $lineItem.product}}
                "product": {
                    "id": "{{ $product.id }}",
                    "title": "{{ $product.title }}",
                    "status": "{{ $product.status }}",
                    "created_at": {{ toJson $product.createdAt }},
                    "product_type": "{{ $product.productType }}",
                    "vendor": "{{ $product.vendor }}",
                    "updated_at": {{ toJson $product.updatedAt }},
                    "tags": {{ toJson $product.tags }},
                    "options": {{ toJson $product.options }},
                    "variants": [
                        {{/* Iterate through all order line item variants. Simplify edges and nodes out of the response. */}}
                        {{- range $index2, $variantsEdge := $product.variants.edges -}}
                        {{- $variant := $variantsEdge.node}}
                        {
                            "created_at": {{ toJson $variant.createdAt }},
                            "id": "{{ $variant.id }}",
                            "price": "{{ $variant.price }}",
                            "title": "{{ $variant.title }}",
                            "updated_at": {{ toJson $variant.updatedAt }}   
                        }          
                        {{- if lt (add $index2 1) (len $product.variants.edges) -}},{{- end -}}
                        {{- end -}}
                    ]
                },    
                "quantity": {{ $lineItem.quantity }},
                "sku": "{{ $lineItem.sku }}",
                "gift_card": "{{ $lineItem.isGiftCard }}",
                "total_discount": "{{ $lineItem.totalDiscountSet.presentmentMoney.amount }}",
                {{- if ne $lineItem.variant nil -}}
                "variant_id": {{ toJson $lineItem.variant.id }},
                "variant_title": {{ toJson $lineItem.variant.title }},
                {{- end -}}
                "vendor": "{{ $lineItem.vendor }}"
            }
            {{- if lt (add $index 1) (len $order.lineItems.edges) -}},{{- end -}}
            {{- end -}}
        ],
        "transactions": [
            {{- range $index1, $transactionsEdge := $order.transactions -}}
            {{- $transaction := $transactionsEdge}}
            {
                "id": "{{ $transaction.id }}",
                "created_at": {{ toJson $transaction.created_at }},
                "kind": "{{ $transaction.id }}",
                "gateway": "{{ $transaction.gateway }}",
                "parent_transaction": {{ toJson $transaction.parentTransaction }},
                "amount": "{{ $transaction.amountSet.shopMoney.amount }}",
                "currency_code": "{{ $transaction.amountSet.shopMoney.currencyCode }}"
            }
            {{- if lt (add $index1 1) (len $order.transactions) -}},{{- end -}}
            {{- end -}}
        ],
        "shipping_lines": [
            {{- range $index, $shippingItemsEdge := $order.shippingLines.edges -}}
            {{- $lineItem := $shippingItemsEdge.node}}
            {
                "carrierIdentifier": {{ toJson $lineItem.carrierIdentifier }},
                "code": {{ toJson $lineItem.code }},
                "custom": {{ $lineItem.custom }},
                "id": "{{ $lineItem.id }}",
                "phone": {{ toJson $lineItem.phone }},
                "source": "{{ $lineItem.source }}",
                "title": "{{ $lineItem.title }}"
            }
            {{- if lt (add $index 1) (len $order.shippingLines.edges) -}},{{- end -}}
            {{- end -}}
        ]

    }
}
{{- end -}}

{{- define "orderFulfillments"}}
    {{- $order := . -}}
    "fulfillments_count": {{- toJson $order.fulfillmentsCount.count -}},
    "fulfillable": {{- toJson $order.fulfillable -}},
    "fulfillments": [
        {{- range $index1, $fulfillment := $order.fulfillments -}}
        {
            "id": "{{ $fulfillment.id }}",
            "created_at": "{{ $fulfillment.createdAt }}",
            "updated_at": "{{ $fulfillment.updatedAt }}",
            "delivered_at": {{ toJson $fulfillment.deliveredAt }},
            "display_status": {{ toJson $fulfillment.displayStatus }},
            "estimated_delivery_at": {{ toJson $fulfillment.estimatedDeliveryAt }},
            "in_transit_at": {{ toJson $fulfillment.inTransitAt }},
            "name": "{{ $fulfillment.name }}",
            "origin_address": 
            {{- with $fulfillment.originAddress -}}
            {
                "address1": {{ toJson $fulfillment.originAddress.address1 }}
                "address2": {{ toJson $fulfillment.originAddress.address2 }}
                "city": {{ toJson $fulfillment.originAddress.city }}
                "country_code": {{ toJson $fulfillment.originAddress.countryCode }}
                "province_code": {{ toJson $fulfillment.originAddress.provinceCode }}
                "zip": {{ toJson $fulfillment.originAddress.zip }}
            },
            {{- else -}}
                null,
            {{- end -}}
            "requires_shipping": {{ $fulfillment.requiresShipping }},
            "status": "{{ $fulfillment.status }}",
            "tracking_info": [
                {{- range $index2, $trackingInfo := $fulfillment.trackingInfo -}}
                {
                    "company": {{ toJson $trackingInfo.company }},
                    "number": {{ toJson $trackingInfo.number }},
                    "url": {{ toJson $trackingInfo.url }}
                }
                {{- if lt (add $index2 1) (len $fulfillment.trackingInfo) -}},{{- end -}}
                {{- end -}}
            ],
            "fulfillment_line_items": [
                {{- range $index3, $fulfillmentLineItem := $fulfillment.fulfillmentLineItems.edges -}}
                {
                    {{- $lineItem := $fulfillmentLineItem.node -}}
                    "id": "{{ $lineItem.id }}",
                    "quantity": "{{ $lineItem.quantity }}",
                    "original_total": {{ toJson $lineItem.originalTotalSet.amount }},
                    "discounted_total": {{ toJson $lineItem.discountedTotalSet.amount }},
                    "line_item_id": "{{ $lineItem.lineItem.id }}"
                }
                {{- if lt (add $index3 1) (len $fulfillment.fulfillmentLineItems.edges) -}},{{- end -}}
                {{- end -}}                
            ]
        }
        {{- if lt (add $index1 1) (len $order.fulfillments) -}},{{- end -}}
        {{- end -}}
    ]
{{- end -}}