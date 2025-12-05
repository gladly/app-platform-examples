{{- /* Handle 204 No Content - no returns found */}}
{{- if eq .response.statusCode 204}}
[]
{{- else if eq .response.statusCode 200}}
{{- /* Build a map of order IDs to order objects for lookup */}}
{{- $ordersMap := dict}}
{{- range $order := .rawData.orders}}
{{- $ordersMap = set $ordersMap $order.id $order}}
{{- end}}
{{- /* Successful response - transform returns array */}}
[
{{- $firstItem := true}}
{{- range $index, $return := .rawData.returns }}
{{- if not $firstItem}},{{end}}
{{- $firstItem = false}}
{{- $linkedOrder := index $ordersMap $return.order.id}}
  {
    "id": "{{$return.id}}",
    "status": "{{$return.status}}",
    "type": "{{$return.type}}",
    "createdAt": "{{$return.createdAt}}",
    "updatedAt": "{{$return.updatedAt}}",
    "order": {
      "id": "{{$return.order.id}}",
      "name": {{if $linkedOrder}}{{toJson $linkedOrder.name}}{{else}}""{{end}},
      "externalId": {{if $linkedOrder}}{{toJson $linkedOrder.externalId}}{{else}}""{{end}}
    },
    "source": {
      "emailAddress": "{{default "" $return.source.emailAddress}}",
      "phoneNumber": "{{default "" $return.source.phoneNumber}}",
      "name": "{{default "" $return.source.name.given}} {{default "" $return.source.name.surname}}",
      "address": "{{default "" $return.source.mailingAddress.line1}}{{if $return.source.mailingAddress.line2}}, {{$return.source.mailingAddress.line2}}{{end}}, {{default "" $return.source.mailingAddress.city}}, {{default "" $return.source.mailingAddress.state}} {{default "" $return.source.mailingAddress.postalCode}}, {{default "" $return.source.mailingAddress.country}}"
    },
    "destination": {
      "phoneNumber": "{{default "" $return.destination.phoneNumber}}",
      "address": "{{default "" $return.destination.mailingAddress.line1}}{{if $return.destination.mailingAddress.line2}}, {{$return.destination.mailingAddress.line2}}{{end}}, {{default "" $return.destination.mailingAddress.city}}, {{default "" $return.destination.mailingAddress.state}} {{default "" $return.destination.mailingAddress.postalCode}}, {{default "" $return.destination.mailingAddress.country}}"
    },
    "items": [
      {{- range $itemIndex, $item := $return.items}}
      {{- if gt $itemIndex 0}},{{end}}
      {
        "id": "{{$item.id}}",
        "sku": "{{default "" $item.sku}}",
        "quantity": {{$item.quantity}},
        "reason": {{toJson (default "" $item.reason)}},
        "customerComment": {{toJson (default "" $item.customerComment)}},
        "status": "{{default "" $item.status}}",
        "greenReturn": {{default false $item.greenReturn}},
        "refund": {
          "amount": "{{default "0.00" $item.refund.amount.amount}}",
          "currency": "{{default "USD" $item.refund.amount.currency}}",
          "type": "{{default "" $item.refund.type}}"
        }
      }
      {{- end}}
    ],
    "totals": {
      "refund": {
        "amount": "{{default "0.00" $return.totals.refund.amount.amount}}",
        "currency": "{{default "USD" $return.totals.refund.amount.currency}}"
      },
      "exchange": {
        "amount": "{{default "0.00" $return.totals.exchange.amount.amount}}",
        "currency": "{{default "USD" $return.totals.exchange.amount.currency}}"
      },
      "storeCredit": {
        "amount": "{{default "0.00" $return.totals.storeCredit.amount.amount}}",
        "currency": "{{default "USD" $return.totals.storeCredit.amount.currency}}"
      },
      "charge": {
        "amount": "{{default "0.00" $return.totals.charge.amount.amount}}",
        "currency": "{{default "USD" $return.totals.charge.amount.currency}}"
      }
    },
    "exchange": {
      "itemCount": {{default 0 $return.exchange.itemCount}},
      "provision": "{{default "" $return.exchange.provision}}",
      "items": [
        {{- range $exIndex, $exItem := $return.exchange.items}}
        {{- if gt $exIndex 0}},{{end}}
        {
          "id": "{{$exItem.id}}",
          "sku": "{{default "" $exItem.sku}}",
          "quantity": {{default 1 $exItem.quantity}},
          "variantName": {{toJson (default "" $exItem.variant.name)}},
          "price": "{{default "0.00" $exItem.price.amount}}",
          "currency": "{{default "USD" $exItem.price.currency}}"
        }
        {{- end}}
      ]
    },
    "shipment": {
      "carrier": "{{default "" $return.shipment.carrier}}",
      "status": "{{default "" $return.shipment.status}}",
      "tracker": "{{default "" $return.shipment.tracker}}",
      "trackingUrl": "{{default "" $return.shipment.trackingUrl}}",
      "postageLabel": "{{default "" $return.shipment.postageLabel}}"
    },
    "giftCards": [
      {{- range $gcIndex, $gc := $return.giftCards}}
      {{- if gt $gcIndex 0}},{{end}}
      {
        "code": "{{$gc.code}}",
        "amount": "{{default "0.00" $gc.amount.amount}}",
        "currency": "{{default "USD" $gc.amount.currency}}"
      }
      {{- end}}
    ],
    "notes": [
      {{- range $noteIndex, $note := $return.notes}}
      {{- if gt $noteIndex 0}},{{end}}
      {{toJson (default "" $note.message)}}
      {{- end}}
    ]
  }
{{- end}}
]
{{- else}}
{{- printf "unexpected response status code: %d" .response.statusCode | fail}}
{{- end}}
