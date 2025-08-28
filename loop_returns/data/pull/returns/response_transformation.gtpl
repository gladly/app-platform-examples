{{- /* Successful response main transformation */ -}}
[
{{- /* iterate over all returns */}}
{{- $customerEmails := .customer.emailAddresses}}
{{- $firstItem := true}}
{{- range $dataIndex, $return := .rawData.data }}
{{- /* validate if the returned customer email matches a gladly email */}}
{{- if not (has (lower $return.customer) $customerEmails)}}
	{{- continue}}
{{- end}}
{{- /* prepend comma to object definition if not first object */}}
{{- if not $firstItem}},{{end}}
{{- $firstItem = false}}
  {
    "id": "{{$return.id}}",
    "state": "{{$return.state}}",
    "updatedAt": "{{$return.updated_at}}",
    "createdAt": "{{$return.created_at}}",
    "editedAt": {{toJson $return.edited_at}},
    "currency": "{{default "USD" $return.currency}}",
    "url": "https://admin.loopreturns.com/returns/{{$return.id}}",
    "outcome": "{{if .outcome}}{{title (.outcome | replace "+" ", ")}}{{end}}",
    "customerEmail": "{{$return.customer}}",
    "order": {
      "loopId": "{{$return.order_id}}",
      "name": "{{$return.order_name}}",
      "providerId": "{{default "" $return.provider_order_id}}",
      "number": "{{default "" $return.order_number}}"
    },
    "return": {
      "productTotal": "{{$return.return_product_total}}",
      "discountTotal": "{{$return.return_discount_total}}",
      "taxTotal": "{{$return.return_tax_total}}",
      "total": "{{$return.return_total}}",
      "items": [
        {{- range $itemIndex, $item := $return.line_items}}
        {{/* prepend comma to object definition if not first object */}}
        {{- if gt $itemIndex 0}},{{end}}
        {
          "id": "{{$item.line_item_id}}",
          "providerId":  "{{$item.provider_line_item_id}}",
          "productId": "{{$item.product_id}}",
          "variantId": "{{default "" $item.variant_id}}",
          "sku": "{{default "" $item.sku}}",
          "title": {{toJson $item.title}},
          "returnReason": {{toJson $item.return_reason}},
          "parentReturnReason": {{toJson $item.parent_return_reason}},
          "returnedAt": {{template "normalizeTimestamp" $item.returned_at}},
          "outcome": "{{$item.outcome}}",
          "comment": {{toJson $item.return_comment}},
          "cost": {
            "price": "{{$item.price}}",
            "discount": "{{$item.discount}}",
            "tax": "{{$item.tax}}",
            "total": "{{$item.refund}}"
          }
        }
        {{end -}}
      ]
    },
    "exchange": {
      "productTotal": "{{$return.exchange_product_total}}",
      "discountTotal": "{{$return.exchange_discount_total}}",
      "taxTotal": "{{$return.exchange_tax_total}}",
      "total": "{{$return.exchange_total}}",
      "items": [
        {{- range $itemIndex, $item := $return.exchanges}}
        {{/* prepend comma to object definition if not first object */}}
        {{- if gt $itemIndex 0}},{{end}}
        {
          "id": "{{$item.exchange_id}}",
          "productId": "{{$item.product_id}}",
          "variantId": "{{default "" $item.variant_id}}",
          "orderName": "{{default "" $item.exchange_order_name}}",
          "orderId": "{{default "" (int $item.exchange_order_id)}}",
          "sku": "{{default "" $item.sku}}",
          "title": {{toJson $item.title}},
          "isOutOfStock": {{$item.out_of_stock}},
          "outOfStockResolution": "{{default "" $item.out_of_stock_resolution}}",
          "cost": {
            "price": "{{$item.price}}",
            "discount": "{{$item.discount}}",
            "tax": "{{$item.tax}}",
            "total": "{{$item.total}}"
          }
        }
        {{end -}}
      ]
    },
    "costBreakdown": {
      "handlingFee": "{{$return.handling_fee}}",
      "giftCardAmount": "{{$return.gift_card}}",
      "refund": "{{$return.refund}}",
      "upsell": "{{$return.upsell}}"
    },
    "shipping": {
      "carrier": "{{$return.carrier}}",
      "trackingNumber": "{{$return.tracking_number}}",
      "labelStatus": "{{$return.label_status}}",
      "labelUpdatedAt": {{if eq $return.label_updated_at "N/A"}}null{{else}}"{{$return.label_updated_at}}"{{end}},
      "statusPageUrl": "{{$return.status_page_url}}",
      "returnMethod": "{{default "" $return.return_method}}",
      "destinationId": "{{$return.destination_id}}"
    }
  }
{{end -}}
]

{{- /* Convert any incoming timestamps to an ISO8601 format */}}
{{- define "normalizeTimestamp"}}
	{{- if not . -}}
		null
	{{- else}}
		{{- if regexMatch "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}" . -}}
			"{{printf "%sZ" (replace " " "T" .)}}"
		{{- else}}
			{{- $timeStamp := toDate "2006-01-02T15:04:05Z07:00" .}}
			{{- if not $timeStamp.IsZero -}}
				"{{.}}"
			{{- else -}}
				null
			{{- end}}
		{{- end}}
	{{- end}}
{{- end}}