{{- /* Field/target pairing is dictated by Ordergroove:
       "If using target: \"item\", field should be 'total_price'. If using
        target: \"order\", field should be 'sub_total' or 'shipping_total'."
       `type` must be the literal string 'Discount'. */ -}}
{{- /* Every caller-supplied value is emitted with toJson, which brings its own
       surrounding quotes and escapes quotes, backslashes and newlines. `description`
       and `reason` are free text from Team Assist or the agent form: hand-quoting
       them turns `Late "holiday" shipment` into a malformed body and a 400 from
       Ordergroove. Only the template's own literals below stay hand-quoted. */ -}}
{{- $itemId := printf "%v" (default "" .inputs.itemId) -}}
{{- $orderId := printf "%v" (default "" .inputs.orderId) -}}
{{- $isItem := ne $itemId "" -}}
{{- $discountType := printf "%v" .inputs.discountType -}}
{{- $ogDiscountType := "Discount Amount" -}}
{{- if eq $discountType "percent" -}}{{- $ogDiscountType = "Discount Percent" -}}{{- end -}}
{
  "merchant": {{ printf "%v" .inputs.merchantId | toJson }},
  "customer": {{ printf "%v" .inputs.customerId | toJson }},
  {{- if $isItem }}
  "item": {{ $itemId | toJson }},
  {{- else }}
  "order": {{ $orderId | toJson }},
  {{- end }}
  {{- with .inputs.description }}
  "description": {{ . | toJson }},
  {{- end }}
  "incentive": {
    "name": {{ .inputs.reason | default "Customer service goodwill discount" | toJson }},
    "discount_type": {{ $ogDiscountType | toJson }},
    "value": {{ printf "%v" .inputs.value | toJson }},
    "field": "{{if $isItem}}total_price{{else}}sub_total{{end}}",
    "type": "Discount",
    "target": "{{if $isItem}}item{{else}}order{{end}}"
  }
}
