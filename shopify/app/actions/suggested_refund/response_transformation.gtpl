{{- /* Build dicts field-by-field (not a toJson passthrough) so the emitted shape matches the schema
       exactly — `appcfg test` deep-equals raw output, so any undeclared field would fail it. */ -}}
{{- $errors := .rawData.errors -}}
{{- $hasErrors := and $errors (gt (len $errors) 0) -}}

{{ if (ne .response.statusCode 200) -}}
    {{- print (toJson .rawData.errors) | fail -}}
{{- else if $hasErrors -}}
{
    "suggestedRefund": null,
    "errors": [
        {{- range $i, $e := $errors -}}
        {{- if $i }},{{ end }}
        { "message": {{ toJson $e.message }}, "code": {{ toJson $e.extensions.code }} }
        {{- end }}
    ]
}
{{- else if eq .rawData.data.order nil -}}
{
    "suggestedRefund": null,
    "errors": [{ "message": "Order is not found", "code": "not_found" }]
}
{{- else -}}
{{- $sr := .rawData.data.order.suggestedRefund -}}

{{- /* refund lines: lineItemId from lineItem.id; subtotalSet (MoneySet) passed through. */ -}}
{{- $lines := list -}}
{{- range $sr.refundLineItems -}}
  {{- $li := dict "quantity" .quantity -}}
  {{- if .lineItem -}}
    {{- $_ := set $li "lineItemId" .lineItem.id -}}
    {{- if .lineItem.name -}}{{- $_ := set $li "name" .lineItem.name -}}{{- end -}}
    {{- if .lineItem.sku -}}{{- $_ := set $li "sku" .lineItem.sku -}}{{- end -}}
  {{- end -}}
  {{- if .subtotalSet -}}{{- $_ := set $li "subtotalSet" .subtotalSet -}}{{- end -}}
  {{- $lines = append $lines $li -}}
{{- end -}}

{{- /* transactions: parentTransaction { id }, accountNumber, gateway, formattedGateway, amountSet
       (MoneySet). */ -}}
{{- $txns := list -}}
{{- range $sr.suggestedTransactions -}}
  {{- $t := dict "gateway" .gateway -}}
  {{- if .parentTransaction -}}{{- $_ := set $t "parentTransaction" (dict "id" .parentTransaction.id) -}}{{- end -}}
  {{- if .accountNumber -}}{{- $_ := set $t "accountNumber" .accountNumber -}}{{- end -}}
  {{- if .formattedGateway -}}{{- $_ := set $t "formattedGateway" .formattedGateway -}}{{- end -}}
  {{- if .amountSet -}}{{- $_ := set $t "amountSet" .amountSet -}}{{- end -}}
  {{- $txns = append $txns $t -}}
{{- end -}}

{{- $refund := dict
    "refundLineItems" $lines
    "suggestedTransactions" $txns -}}
{{- if $sr.amountSet -}}{{- $_ := set $refund "amountSet" $sr.amountSet -}}{{- end -}}
{{- if $sr.maximumRefundableSet -}}{{- $_ := set $refund "maximumRefundableSet" $sr.maximumRefundableSet -}}{{- end -}}
{{- if $sr.subtotalSet -}}{{- $_ := set $refund "subtotalSet" $sr.subtotalSet -}}{{- end -}}
{{- if $sr.totalTaxSet -}}{{- $_ := set $refund "totalTaxSet" $sr.totalTaxSet -}}{{- end -}}
{{- if $sr.shipping -}}
  {{- $sh := dict -}}
  {{- if $sr.shipping.amountSet -}}{{- $_ := set $sh "amountSet" $sr.shipping.amountSet -}}{{- end -}}
  {{- if $sr.shipping.maximumRefundableSet -}}{{- $_ := set $sh "maximumRefundableSet" $sr.shipping.maximumRefundableSet -}}{{- end -}}
  {{- if $sr.shipping.taxSet -}}{{- $_ := set $sh "taxSet" $sr.shipping.taxSet -}}{{- end -}}
  {{- $_ := set $refund "shipping" $sh -}}
{{- end -}}

{
    "suggestedRefund": {{ toJson $refund }},
    "errors": []
}
{{- end -}}
