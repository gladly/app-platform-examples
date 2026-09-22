{{- /* Swap a subscription's item to a different Shopify variant (free-text variant id; there is no
       product picker -- the platform has no live catalog source). Filter to active + swappable
       subscriptions: is_swappable is null-safe (missing/null is treated as swappable so valid subs
       aren't hidden; only an explicit false excludes), mirroring rescheduleNextCharge's is_prepaid
       pattern. No price override -- that field was dropped in v1 to close a bypass around the
       guarded adjustSubscriptionPrice cap; swap first, then use Adjust price if needed. */ -}}
{{- $subs := list}}
{{- $nonSwappableSeen := false}}
{{- if and .data .data.subscriptions}}
  {{- range .data.subscriptions}}
    {{- if eq (.status | toString) "active"}}
      {{- if eq .is_swappable false}}
        {{- $nonSwappableSeen = true}}
      {{- else}}
        {{- $subs = append $subs .}}
      {{- end}}
    {{- end}}
  {{- end}}
{{- end}}
{
  "title": "Swap subscription item",
{{- if $subs}}
  "submitButton": "Swap item",
{{end -}}
  "closeButton": "Close",
  "sections": [
{{- if $subs}}
    {
      "type": "input",
      "label": "Subscription",
      "attr": "subscriptionId",
      "input": {
        "type": "select",
        "placeholder": "Select the subscription to change",
        "options": [
        {{- range $i, $s := $subs}}
          {{- if gt $i 0}},{{end}}
          {{- $title := default "Subscription" $s.product_title}}
          {{- $context := printf "qty %s" (default "1" (toString $s.quantity))}}
          {{- if and $s.variant_title (ne (toString $s.variant_title) "")}}
            {{- $context = printf "%s, %s" (toString $s.variant_title) $context}}
          {{- end}}
          {{- if and $s.sku (ne (toString $s.sku) "")}}
            {{- $context = printf "%s, SKU %s" $context (toString $s.sku)}}
          {{- end}}
          {{- $label := printf "%s — #%s — %s" $title $s.id $context}}
          {
            "text": {{ $label | toJson }},
            "value": {{ $s.id | toJson }}
          }
        {{- end}}
        ],
        "optional": false
      }
    },
    {
      "type": "input",
      "label": "Shopify variant ID",
      "attr": "externalVariantId",
      "input": { "type": "text", "placeholder": "e.g. 51139736109343", "optional": false },
      "hint": "Paste the destination Shopify VARIANT id (Shopify admin → Products → the variant → last number in the URL) — not the product id. Recharge fills in product, price, and SKU from it."
    },
    {
      "type": "input",
      "label": "Quantity",
      "attr": "quantity",
      "input": { "type": "text", "placeholder": "Leave blank to keep current", "optional": true }
    }
{{- else if $nonSwappableSeen}}
    {
      "type": "text",
      "text": "This customer has no swappable subscriptions. One-time or non-swappable items must be changed in Recharge directly."
    }
{{- else}}
    {
      "type": "text",
      "text": "This customer has no active subscriptions to modify (cancelled subscriptions must be changed in Recharge)."
    }
{{- end}}
  ]
}
