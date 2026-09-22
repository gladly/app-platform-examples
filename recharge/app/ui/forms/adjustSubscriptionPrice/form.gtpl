{{- /* Set a subscription's line-item price. Guarded: the price cap and the approve confirmation are
       enforced by the action (the form cannot read integration.configuration). Active subs only. */ -}}
{{- $subs := list}}
{{- if and .data .data.subscriptions}}
  {{- range .data.subscriptions}}
    {{- if eq (.status | toString) "active"}}{{- $subs = append $subs .}}{{- end}}
  {{- end}}
{{- end}}
{
  "title": "Adjust subscription price",
{{- if $subs}}
  "submitButton": "Update price",
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
        "placeholder": "Select the subscription",
        "options": [
        {{- range $i, $s := $subs}}
          {{- if gt $i 0}},{{end}}
          {{- $title := default "Subscription" $s.product_title}}
          {{- $price := default "n/a" (toString $s.price)}}
          {{- $currency := default "" (toString $s.presentment_currency)}}
          {{- $context := printf "current price %s" $price}}
          {{- if ne $currency ""}}{{- $context = printf "%s %s" $context $currency}}{{- end}}
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
      "label": "New price",
      "attr": "price",
      "input": { "type": "text", "placeholder": "e.g. 12.00", "optional": false },
      "hint": "Enter the exact price the customer requested. Amounts above the store's limit are rejected — the ceiling appears in the error if you exceed it."
    },
    {
      "type": "input",
      "label": "Confirmation",
      "attr": "confirmationCopy",
      "input": { "type": "text", "placeholder": "approve", "optional": false },
      "hint": "Re-read the subscription and new price above. Type approve only if they match what the customer requested."
    }
{{- else}}
    {
      "type": "text",
      "text": "This customer has no active subscriptions to modify (cancelled subscriptions must be changed in Recharge)."
    }
{{- end}}
  ]
}
