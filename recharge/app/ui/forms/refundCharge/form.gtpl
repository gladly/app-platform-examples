{{- /* Refund a paid charge (partial or full). Only charges that can actually be refunded are
       offered: status success or partially_refunded with a remaining refundable balance
       (total_price - total_refunds > 0, computed with subf so the decimals survive -- Sprig's plain
       sub is integer-only). Each option packs {chargeId, refundableAmount} as its value
       (manageNextChargeSkip's idiom); action_inputs.gtpl unpacks it. refundableAmount lets the action
       cap a full refund against that charge's remaining balance -- forms can't read the merchant cap,
       so the action enforces it.

       Shopify Payments gating: Recharge structurally cannot refund a charge processed by Shopify
       Payments (Shopify Checkout Integration -- "Refunds ... must be initiated from Shopify"), so
       those charges are EXCLUDED from the picker and counted, with an explanatory line so the agent
       knows why a paid order isn't listed (Gorgias gates its refund button the same way). The gate
       fails OPEN for any other/unknown processor (stripe, braintree, null, ...) -- Recharge's own
       rejection remains the backstop, and we must not wrongly block a refundable charge. */ -}}
{{- $opts := list}}
{{- $gated := 0}}
{{- if and .data .data.charges}}
  {{- range $c := .data.charges}}
    {{- $st := $c.status | toString}}
    {{- if or (eq $st "success") (eq $st "partially_refunded")}}
      {{- $remaining := subf ($c.total_price | default "0") ($c.total_refunds | default "0")}}
      {{- if gt $remaining 0.0}}
        {{- if eq ($c.payment_processor | toString) "shopify_payments"}}
          {{- $gated = add1 $gated}}
        {{- else}}
          {{- $remainingStr := printf "%.2f" $remaining}}
          {{- $when := "n/a"}}
          {{- if $c.processed_at}}{{- $when = substr 0 10 (toString $c.processed_at)}}
          {{- else if $c.scheduled_at}}{{- $when = toString $c.scheduled_at}}{{- end}}
          {{- $label := printf "Paid order — %s, %s refundable (#%s)" $when $remainingStr $c.id}}
          {{- $opts = append $opts (dict "chargeId" $c.id "refundableAmount" $remainingStr "label" $label)}}
        {{- end}}
      {{- end}}
    {{- end}}
  {{- end}}
{{- end}}
{
  "title": "Refund a charge",
{{- if $opts}}
  "submitButton": "Refund",
{{end -}}
  "closeButton": "Close",
  "sections": [
{{- if $opts}}
{{- if gt $gated 0}}
    {
      "type": "text",
      "text": {{ printf "%d paid order(s) were processed by Shopify Payments and must be refunded in Shopify — they aren't listed here." $gated | toJson }}
    },
{{- end}}
    {
      "type": "input",
      "label": "Charge",
      "attr": "chargeSelection",
      "input": {
        "type": "select",
        "placeholder": "Select the order to refund",
        "options": [
        {{- range $i, $o := $opts}}
          {{- if gt $i 0}},{{end}}
          {
            "text": {{ $o.label | toJson }},
            {{- $value := dict "chargeId" $o.chargeId "refundableAmount" $o.refundableAmount}}
            "value": {{toJson $value | toJson}}
          }
        {{- end}}
        ],
        "optional": false
      }
    },
    {
      "type": "input",
      "label": "Refund type",
      "attr": "fullRefund",
      "input": {
        "type": "select",
        "placeholder": "Full or partial",
        "options": [
          { "text": "Full refund (entire remaining balance)", "value": "true" },
          { "text": "Partial refund (enter an amount)", "value": "false" }
        ],
        "optional": false
      }
    },
    {
      "type": "input",
      "label": "Amount",
      "attr": "amount",
      "input": { "type": "text", "placeholder": "e.g. 10.00", "optional": true },
      "hint": "Required for a partial refund; leave blank for a full refund. Larger than the order's refundable balance or the merchant's configured maximum is rejected."
    },
    {
      "type": "input",
      "label": "Confirmation",
      "attr": "confirmationCopy",
      "input": { "type": "text", "placeholder": "approve", "optional": false },
      "hint": "Type approve to confirm. Refunds are irreversible. Only refund an amount the customer is actually owed, after verifying their identity."
    }
{{- else if gt $gated 0}}
    {
      "type": "text",
      "text": "This customer's paid orders were processed by Shopify Payments. Refund them in Shopify — Recharge can't refund Shopify Payments charges."
    }
{{- else}}
    {
      "type": "text",
      "text": "This customer has no paid charges with a remaining refundable balance. A charge processed through Shopify's checkout must be refunded in Shopify."
    }
{{- end}}
  ]
}
