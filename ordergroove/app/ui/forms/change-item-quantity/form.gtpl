{{- /* Item picker, from the `items` pull, restricted to items on an ACTIONABLE order.
       The items pull deliberately fetches line items for up to ten already-completed
       (SUCCESS/CANCELLED/MERGED) orders so the profile card can show order history. An
       Item carries `order` but no order status, so the form also requests `orders` and
       builds the actionable set from the isActionable boolean the orders pull computes
       (UNSENT, not locked, not cancelled). Without this, the picker offers items on
       shipped orders and changeItemQuantity returns a 400 from Ordergroove.
       Labelling caveat: an Item carries `product` - an external product id - but no
       product NAME, and the products pull declares dependsOnDataTypes [ordergroove_item,
       ordergroove_subscription], so `items` cannot depend on `products` without a cycle.
       productDetail is tried first in case the child resolver is available to formData;
       the fallback is id + qty + line total. */ -}}
{{- $actionableOrders := dict -}}
{{- range $order := .data.orders -}}
  {{- if eq (printf "%v" $order.isActionable) "true" -}}
    {{- $_ := set $actionableOrders (toString $order.publicId) true -}}
  {{- end -}}
{{- end -}}
{{- $options := list -}}
{{- range $item := .data.items -}}
  {{- if hasKey $actionableOrders (toString $item.order) -}}
  {{- $name := "" -}}
  {{- if $item.productDetail -}}
    {{- if $item.productDetail.name -}}{{- $name = toString $item.productDetail.name -}}{{- end -}}
  {{- end -}}
  {{- if eq $name "" -}}{{- $name = printf "Product %s" (toString $item.product) -}}{{- end -}}
  {{- $label := $name -}}
  {{- if $item.quantityLabel -}}{{- $label = printf "%s - qty %s" $label (toString $item.quantityLabel) -}}{{- end -}}
  {{- if $item.totalPrice -}}{{- $label = printf "%s - %s" $label (toString $item.totalPrice) -}}{{- end -}}
  {{- $label = printf "%s [order %s]" $label (toString $item.order) -}}
  {{- $options = append $options (dict "text" $label "value" (toString $item.publicId)) -}}
  {{- end -}}
{{- end -}}
{
  "title": "Change quantity of one item",
  {{- if gt (len $options) 0 }}
  "submitButton": "Change item quantity",
  {{- else }}
  "closeButton": "Close",
  "submitButton": "Unavailable",
  {{- end }}
  "sections": [
    {{- if eq (len $options) 0 }}
    {
      "type": "text",
      "text": "This customer has no order items available to change."
    }
    {{- else }}
    {
      "type": "input",
      "label": "Item",
      "attr": "itemId",
      "input": {
        "type": "select",
        "placeholder": "Choose the item",
        "optional": false,
        "options": {{ $options | toJson }}
      }
    },
    {
      "type": "input",
      "label": "New quantity",
      "attr": "quantity",
      "input": {
        "type": "text",
        "placeholder": "e.g. 2"
      },
      "hint": "Applies to this one order only. To change every future order, use Change subscription quantity."
    }
    {{- end }}
  ]
}
