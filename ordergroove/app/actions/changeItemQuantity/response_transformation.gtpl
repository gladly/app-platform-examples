{{- if and .response (ne .response.statusCode 200) -}}
  {{- if eq .response.statusCode 404 -}}
    {{ stop (printf "Item %s does not exist in Ordergroove." .inputs.itemId) }}
  {{- end -}}
  {{- if eq .response.statusCode 400 -}}
    {{- if .rawData.detail -}}
      {{ stop (printf "Change item quantity: %s" .rawData.detail) }}
    {{- end -}}
    {{- range $field, $msgs := .rawData -}}
      {{- if kindIs "slice" $msgs -}}
        {{- if gt (len $msgs) 0 -}}
          {{ stop (printf "Change item quantity: %s" (join ", " $msgs)) }}
        {{- end -}}
      {{- end -}}
    {{- end -}}
    {{ stop "Change item quantity: Ordergroove rejected the change. Item quantity cannot be changed on a prepaid subscription." }}
  {{- end -}}
  {{- /* Same staleness exposure as deleteOrderItem: Ordergroove 500s with an empty body
         when the item's parent order has left UNSENT since the card was loaded. See the
         comment in deleteOrderItem/response_transformation.gtpl. */ -}}
  {{- if ge .response.statusCode 500 -}}
    {{ stop "Change item quantity: Ordergroove would not change this order. It is usually because the order is no longer open - it may have been placed, rejected or skipped since this card was loaded. Refresh the customer's profile to see the current orders, then try again." }}
  {{- end -}}
  {{ fail (printf "Change item quantity failed: %d %s" .response.statusCode .response.body) }}
{{- end -}}
{{- $i := .rawData -}}
{{- if $i.order_updated -}}{{- $_ := set $i "order_updated" ($i.order_updated | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}{{- end -}}
{{- if and $i.first_placed (ne (printf "%v" $i.first_placed) "null") (ne (printf "%v" $i.first_placed) "") -}}{{- $_ := set $i "first_placed" ($i.first_placed | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}{{- end -}}
{{- $_ := set $i "publicId" $i.public_id -}}
{{- $_ := set $i "totalPrice" $i.total_price -}}
{{- if ne $i.quantity nil -}}{{- $_ := set $i "quantityLabel" (printf "%d" (int $i.quantity)) -}}{{- end -}}
{{toJson $i}}
