{{- if ne .response.statusCode 200 -}}
    {{- if .rawData.message -}}
        {{ printf "Redo API error: %s" .rawData.message | stop }}
    {{- else -}}
        {{ printf "Redo API returned status code %d" .response.statusCode | stop }}
    {{- end -}}
{{- else -}}
[
{{- $firstReturn := true }}
{{- range $return := .rawData.returns }}
    {{- if not $firstReturn }},{{ end }}
    {{- $firstReturn = false }}
    {{- $orderName := "" -}}
    {{- if and $return.order $return.order.id -}}
        {{- range $order := $.rawData.orders -}}
            {{- if eq $order.id $return.order.id -}}
                {{- $orderName = $order.name -}}
            {{- end -}}
        {{- end -}}
    {{- end -}}
    {
        "id": {{ $return.id | toJson }},
        "orderName": {{- if $orderName -}}{{ $orderName | toJson }}{{- else -}}{{ $return.id | toJson }}{{- end -}},
        "createdAt": {{ $return.createdAt | toJson }},
        "updatedAt": {{ $return.updatedAt | toJson }},
        "status": {{ $return.status | toJson }},
        "type": {{ $return.type | toJson }},
        "source": {{- if $return.source -}}
        {
            "name": {{- if $return.source.name -}}
            {
                "given": {{ $return.source.name.given | toJson }},
                "surname": {{ $return.source.name.surname | toJson }}
            }
            {{- else -}}null{{- end -}},
            "emailAddress": {{ $return.source.emailAddress | toJson }},
            "phoneNumber": {{ $return.source.phoneNumber | toJson }},
            "mailingAddress": {{- if $return.source.mailingAddress -}}
            {
                "line1": {{ $return.source.mailingAddress.line1 | toJson }},
                "line2": {{ $return.source.mailingAddress.line2 | toJson }},
                "city": {{ $return.source.mailingAddress.city | toJson }},
                "state": {{ $return.source.mailingAddress.state | toJson }},
                "postalCode": {{ $return.source.mailingAddress.postalCode | toJson }},
                "country": {{ $return.source.mailingAddress.country | toJson }}
            }
            {{- else -}}null{{- end -}}
        }
        {{- else -}}null{{- end -}},
        "destination": {{- if $return.destination -}}
        {
            "mailingAddress": {{- if $return.destination.mailingAddress -}}
            {
                "line1": {{ $return.destination.mailingAddress.line1 | toJson }},
                "line2": {{ $return.destination.mailingAddress.line2 | toJson }},
                "city": {{ $return.destination.mailingAddress.city | toJson }},
                "state": {{ $return.destination.mailingAddress.state | toJson }},
                "postalCode": {{ $return.destination.mailingAddress.postalCode | toJson }},
                "country": {{ $return.destination.mailingAddress.country | toJson }}
            }
            {{- else -}}null{{- end -}},
            "phoneNumber": {{ $return.destination.phoneNumber | toJson }}
        }
        {{- else -}}null{{- end -}},
        "order": {{- if $return.order -}}
        {
            "id": {{ $return.order.id | toJson }}
        }
        {{- else -}}null{{- end -}},
        "items": [
        {{- $firstItem := true }}
        {{- range $item := $return.items }}
            {{- if not $firstItem }},{{ end }}
            {{- $firstItem = false }}
            {{- $productName := "" -}}
            {{- if $item.productId -}}
                {{- range $order := $.rawData.orders -}}
                    {{- range $orderItem := $order.items -}}
                        {{- if and $orderItem.product (eq $orderItem.product.externalId $item.productId) -}}
                            {{- $productName = $orderItem.product.name -}}
                        {{- end -}}
                    {{- end -}}
                {{- end -}}
            {{- end -}}
            {
                "id": {{ $item.id | toJson }},
                "productName": {{- if $productName -}}{{ $productName | toJson }}{{- else -}}{{ $item.id | toJson }}{{- end -}},
                "orderItem": {{- if $item.orderItem -}}
                {
                    "id": {{ $item.orderItem.id | toJson }},
                    "line_item_id": {{ $item.orderItem.line_item_id | toJson }}
                }
                {{- else -}}null{{- end -}},
                "quantity": {{ $item.quantity | default 0 }},
                "reason": {{ $item.reason | toJson }},
                "customerComment": {{ $item.customerComment | toJson }},
                "greenReturn": {{ $item.greenReturn | default false }},
                "productAdjustment": {{ $item.productAdjustment | toJson }},
                "productId": {{ $item.productId | toJson }},
                "reasonCode": {{ $item.reasonCode | toJson }},
                "reasonCodes": {{ $item.reasonCodes | default (list) | toJson }},
                "reasons": {{ $item.reasons | default (list) | toJson }},
                "refund": {{- if $item.refund -}}
                {
                    "amount": {{- if $item.refund.amount -}}
                    {
                        "amount": {{ $item.refund.amount.amount | toJson }},
                        "currency": {{ $item.refund.amount.currency | toJson }}
                    }
                    {{- else -}}null{{- end -}},
                    "type": {{ $item.refund.type | toJson }}
                }
                {{- else -}}null{{- end -}},
                "sku": {{ $item.sku | toJson }},
                "status": {{ $item.status | toJson }},
                "variantId": {{ $item.variantId | toJson }},
                "exchangeItem": {{- if $item.exchangeItem -}}
                {
                    "product": {{- if $item.exchangeItem.product -}}
                    {
                        "name": {{ $item.exchangeItem.product.name | toJson }},
                        "externalId": {{ $item.exchangeItem.product.externalId | toJson }}
                    }
                    {{- else -}}null{{- end -}},
                    "quantity": {{ $item.exchangeItem.quantity | default 0 }},
                    "variant": {{- if $item.exchangeItem.variant -}}
                    {
                        "name": {{ $item.exchangeItem.variant.name | toJson }},
                        "externalId": {{ $item.exchangeItem.variant.externalId | toJson }}
                    }
                    {{- else -}}null{{- end -}}
                }
                {{- else -}}null{{- end -}},
                "assessments": [
                {{- $firstAssess := true }}
                {{- range $assess := $item.assessments }}
                    {{- if not $firstAssess }},{{ end }}
                    {{- $firstAssess = false }}
                    {
                        "assignedUser": {{- if $assess.assignedUser -}}
                        {
                            "id": {{ $assess.assignedUser.id | toJson }},
                            "email": {{ $assess.assignedUser.email | toJson }},
                            "firstName": {{ $assess.assignedUser.firstName | toJson }},
                            "lastName": {{ $assess.assignedUser.lastName | toJson }},
                            "name": {{ $assess.assignedUser.name | toJson }}
                        }
                        {{- else -}}null{{- end -}},
                        "responses": [
                        {{- $firstResp := true }}
                        {{- range $resp := $assess.responses }}
                            {{- if not $firstResp }},{{ end }}
                            {{- $firstResp = false }}
                            {
                                "type": {{ $resp.type | toJson }},
                                "value": {{ $resp.value | toJson }}
                            }
                        {{- end }}
                        ]
                    }
                {{- end }}
                ],
                "multipleChoiceQuestions": [
                {{- $firstMcq := true }}
                {{- range $mcq := $item.multipleChoiceQuestions }}
                    {{- if not $firstMcq }},{{ end }}
                    {{- $firstMcq = false }}
                    {
                        "question": {{ $mcq.question | toJson }},
                        "answer": {{ $mcq.answer | toJson }}
                    }
                {{- end }}
                ]
            }
        {{- end }}
        ],
        "giftCards": [
        {{- $firstGc := true }}
        {{- range $gc := $return.giftCards }}
            {{- if not $firstGc }},{{ end }}
            {{- $firstGc = false }}
            {
                "amount": {{- if $gc.amount -}}
                {
                    "amount": {{ $gc.amount.amount | toJson }},
                    "currency": {{ $gc.amount.currency | toJson }}
                }
                {{- else -}}null{{- end -}},
                "code": {{ $gc.code | toJson }},
                "externalId": {{ $gc.externalId | toJson }}
            }
        {{- end }}
        ],
        "compensationMethods": {{ $return.compensationMethods | default (list) | toJson }},
        "exchange": {{- if $return.exchange -}}
        {
            "items": [
            {{- $firstExItem := true }}
            {{- range $exItem := $return.exchange.items }}
                {{- if not $firstExItem }},{{ end }}
                {{- $firstExItem = false }}
                {
                    "id": {{ $exItem.id | toJson }},
                    "quantity": {{ $exItem.quantity | default 0 }},
                    "variant": {{- if $exItem.variant -}}
                    {
                        "name": {{ $exItem.variant.name | toJson }},
                        "externalId": {{ $exItem.variant.externalId | toJson }},
                        "sku": {{ $exItem.variant.sku | toJson }},
                        "weight": {{- if $exItem.variant.weight -}}
                        {
                            "kg": {{ $exItem.variant.weight.kg | default 0 }}
                        }
                        {{- else -}}null{{- end -}}
                    }
                    {{- else -}}null{{- end -}},
                    "originalPrice": {{- if $exItem.originalPrice -}}
                    {
                        "amount": {{ $exItem.originalPrice.amount | toJson }},
                        "currency": {{ $exItem.originalPrice.currency | toJson }}
                    }
                    {{- else -}}null{{- end -}},
                    "price": {{- if $exItem.price -}}
                    {
                        "amount": {{ $exItem.price.amount | toJson }},
                        "currency": {{ $exItem.price.currency | toJson }},
                        "tax": {{ $exItem.price.tax | toJson }}
                    }
                    {{- else -}}null{{- end -}},
                    "product": {{- if $exItem.product -}}
                    {
                        "name": {{ $exItem.product.name | toJson }},
                        "externalId": {{ $exItem.product.externalId | toJson }}
                    }
                    {{- else -}}null{{- end -}}
                }
            {{- end }}
            ],
            "provision": {{ $return.exchange.provision | toJson }},
            "itemCount": {{ $return.exchange.itemCount | default 0 }},
            "order": {{- if $return.exchange.order -}}
            {
                "externalId": {{ $return.exchange.order.externalId | toJson }}
            }
            {{- else -}}null{{- end -}},
            "totalTax": {{- if $return.exchange.totalTax -}}
            {
                "amount": {{ $return.exchange.totalTax.amount | toJson }},
                "currency": {{ $return.exchange.totalTax.currency | toJson }}
            }
            {{- else -}}null{{- end -}}
        }
        {{- else -}}null{{- end -}},
        "externalOrderIds": {{ $return.externalOrderIds | default (list) | toJson }},
        "shopifyOrderIds": {{ $return.shopifyOrderIds | default (list) | toJson }},
        "internalCreatedByName": {{ $return.internalCreatedByName | toJson }},
        "notes": [
        {{- $firstNote := true }}
        {{- range $note := $return.notes }}
            {{- if not $firstNote }},{{ end }}
            {{- $firstNote = false }}
            {
                "message": {{ $note.message | toJson }},
                "image": {{ $note.image | toJson }}
            }
        {{- end }}
        ],
        "shipment": {{- if $return.shipment -}}
        {
            "carrier": {{ $return.shipment.carrier | toJson }},
            "postageLabel": {{ $return.shipment.postageLabel | toJson }},
            "status": {{ $return.shipment.status | toJson }},
            "tracker": {{ $return.shipment.tracker | toJson }},
            "trackingUrl": {{ $return.shipment.trackingUrl | toJson }}
        }
        {{- else -}}null{{- end -}},
        "shipments": [
        {{- $firstShip := true }}
        {{- range $ship := $return.shipments }}
            {{- if not $firstShip }},{{ end }}
            {{- $firstShip = false }}
            {
                "carrier": {{ $ship.carrier | toJson }},
                "postageLabel": {{ $ship.postageLabel | toJson }},
                "status": {{ $ship.status | toJson }},
                "tracker": {{ $ship.tracker | toJson }},
                "trackingUrl": {{ $ship.trackingUrl | toJson }}
            }
        {{- end }}
        ],
        "totals": {{- if $return.totals -}}
        {
            "charge": {{- if $return.totals.charge -}}
            {
                "amount": {{- if $return.totals.charge.amount -}}
                {
                    "amount": {{ $return.totals.charge.amount.amount | toJson }},
                    "currency": {{ $return.totals.charge.amount.currency | toJson }}
                }
                {{- else -}}null{{- end -}}
            }
            {{- else -}}null{{- end -}},
            "exchange": {{- if $return.totals.exchange -}}
            {
                "amount": {{- if $return.totals.exchange.amount -}}
                {
                    "amount": {{ $return.totals.exchange.amount.amount | toJson }},
                    "currency": {{ $return.totals.exchange.amount.currency | toJson }}
                }
                {{- else -}}null{{- end -}}
            }
            {{- else -}}null{{- end -}},
            "refund": {{- if $return.totals.refund -}}
            {
                "amount": {{- if $return.totals.refund.amount -}}
                {
                    "amount": {{ $return.totals.refund.amount.amount | toJson }},
                    "currency": {{ $return.totals.refund.amount.currency | toJson }}
                }
                {{- else -}}null{{- end -}}
            }
            {{- else -}}null{{- end -}},
            "storeCredit": {{- if $return.totals.storeCredit -}}
            {
                "amount": {{- if $return.totals.storeCredit.amount -}}
                {
                    "amount": {{ $return.totals.storeCredit.amount.amount | toJson }},
                    "currency": {{ $return.totals.storeCredit.amount.currency | toJson }}
                }
                {{- else -}}null{{- end -}}
            }
            {{- else -}}null{{- end -}}
        }
        {{- else -}}null{{- end -}}
    }
{{- end }}
]
{{- end -}}