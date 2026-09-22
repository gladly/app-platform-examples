{{- $errors := .rawData.errors -}}
{{- $hasErrors := or (and $errors (gt (len $errors) 0)) false -}}

{{ if (ne .response.statusCode 200) }}
    {{- $error := toJson .rawData.errors -}}
	{{- fail  $error -}}
{{- else if $hasErrors -}}
{
    "errors": [
        {{- range $index, $error := $errors -}}
        {
        "message": "{{- $error.message -}}",
        "code": {{- toJson $error.extensions.code -}}
        }
        {{- if lt (add $index 1) (len $errors) -}},{{- end -}}
        {{- end -}}
    ]
}
{{- else -}}
{{- $job := .rawData.data.job -}}
{{- $out := dict "id" $job.id "done" $job.done -}}
{{- if and (ne $job.query nil) (ne $job.query.order nil) -}}
  {{- $ord := $job.query.order -}}
  {{- /* Flatten each refund into the RefundOverview shape (refundLineItems.nodes -> lineItems,
         transactions.nodes -> transactions). */ -}}
  {{- $refunds := list -}}
  {{- range $ord.refunds -}}
    {{- $rfLines := list -}}
    {{- range .refundLineItems.nodes -}}
      {{- $rfl := dict "quantity" .quantity "restockType" .restockType -}}
      {{- if .lineItem -}}{{- $_ := set $rfl "lineItemId" .lineItem.id -}}{{- end -}}
      {{- $rfLines = append $rfLines $rfl -}}
    {{- end -}}
    {{- $rfTxns := list -}}
    {{- range .transactions.nodes -}}
      {{- $rfTxns = append $rfTxns (dict
          "id" .id "kind" .kind "status" .status "gateway" .gateway
          "formattedGateway" .formattedGateway "accountNumber" .accountNumber "amountSet" .amountSet) -}}
    {{- end -}}
    {{- $refunds = append $refunds (dict
        "id" .id "createdAt" .createdAt "note" .note "totalRefundedSet" .totalRefundedSet
        "lineItems" $rfLines "transactions" $rfTxns) -}}
  {{- end -}}
  {{- $_ := set $out "order" (dict
      "name" $ord.name "cancelledAt" $ord.cancelledAt "cancelReason" $ord.cancelReason
      "refunds" $refunds) -}}
{{- end -}}
{{- toJson $out -}}
{{- end -}}