{{- /* refundLineItems / transactions are flattened from their `nodes`, mirroring
       order_by_identifier's refunds mapping. */ -}}
{{- $errors := .rawData.errors -}}
{{- $hasErrors := and $errors (gt (len $errors) 0) -}}

{{ if (ne .response.statusCode 200) -}}
    {{- print (toJson .rawData.errors) | fail -}}
{{- else if $hasErrors -}}
{
    "order": null,
    "refund": null,
    "errors": [
        {{- range $i, $e := $errors -}}
        {{- if $i }},{{ end }}
        { "message": {{ toJson $e.message }}, "code": {{ toJson $e.extensions.code }} }
        {{- end }}
    ]
}
{{- else -}}
{{- $rc := .rawData.data.refundCreate -}}
{{- $userErrors := $rc.userErrors -}}
{{- if and $userErrors (gt (len $userErrors) 0) -}}
{
    "order": null,
    "refund": null,
    "userErrors": [
        {{- range $i, $e := $userErrors -}}
        {{- if $i }},{{ end }}
        { "message": {{ toJson $e.message }}, "field": {{ toJson $e.field }} }
        {{- end }}
    ]
}
{{- else -}}
{{- $r := $rc.refund -}}
{{- /* refund detail: flatten the line-item and transaction connections to plain lists. */ -}}
{{- $lineItems := list -}}
{{- if $r.refundLineItems -}}
  {{- range $r.refundLineItems.nodes -}}
    {{- $rfl := dict "quantity" .quantity "restockType" .restockType -}}
    {{- if .lineItem -}}{{- $_ := set $rfl "lineItemId" .lineItem.id -}}{{- end -}}
    {{- $lineItems = append $lineItems $rfl -}}
  {{- end -}}
{{- end -}}
{{- $txns := list -}}
{{- if $r.transactions -}}
  {{- range $r.transactions.nodes -}}
    {{- $txns = append $txns (dict
        "id" .id "kind" .kind "status" .status "gateway" .gateway
        "formattedGateway" .formattedGateway "accountNumber" .accountNumber "amountSet" .amountSet) -}}
  {{- end -}}
{{- end -}}
{{- $refund := dict
    "id" $r.id "createdAt" $r.createdAt "note" $r.note "totalRefundedSet" $r.totalRefundedSet
    "lineItems" $lineItems "transactions" $txns -}}
{{- /* order overview sibling: compact post-refund order state. */ -}}
{{- $order := dict -}}
{{- if $r.order -}}
  {{- $order = dict
      "id" $r.order.id "name" $r.order.name
      "displayFinancialStatus" $r.order.displayFinancialStatus
      "totalRefundedSet" $r.order.totalRefundedSet -}}
{{- end -}}
{
    "order": {{ if $r.order }}{{ toJson $order }}{{ else }}null{{ end }},
    "refund": {{ toJson $refund }},
    "errors": []
}
{{- end -}}
{{- end -}}