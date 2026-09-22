{
{{- if .action.result.error}}
  {{- $raw := .action.result.error.error_message}}
  {{- $detail := printf "This subscription may not be swappable (one-time or prepaid items are restricted). Confirm the variant, or have a manager change it in Recharge. Details: %s" $raw}}
  "errors": [
    {
      "attr": "subscriptionId",
      "detail": {{ $detail | toJson }}
    }
  ]
{{- else}}
  {{- $sub := .action.result.subscription}}
  {{- $product := default "the subscription's product" $sub.product_title}}
  {{- $detail := ""}}
  {{- if and $sub.variant_title (ne (toString $sub.variant_title) "")}}
    {{- $detail = printf "Now subscribed to %s (%s), price %s." $product (toString $sub.variant_title) (default "n/a" (toString $sub.price))}}
  {{- else}}
    {{- $detail = printf "Now subscribed to %s, price %s." $product (default "n/a" (toString $sub.price))}}
  {{- end}}
  "message": "Subscription item swapped.",
  "detail": {{ $detail | toJson }}
{{- end}}
}
