{{- /* SECURITY: orderId travels as a GraphQL *variable* (not into the query AST), so GraphQL
       injection isn't possible; we still require a well-formed Order GID so a stray quote can't
       break the JSON, and to give a clean error for an order number passed by mistake. */ -}}
{{ if or (eq .inputs.orderId nil) (eq .inputs.orderId "") }}
	{{ stop "orderId is required (a 'gid://shopify/Order/...' order ID)." }}
{{ end }}
{{ if not (regexMatch `^gid://shopify/Order/[0-9]+$` .inputs.orderId) }}
	{{ stop "orderId must be a Shopify Order GID like 'gid://shopify/Order/1234567890'. This action does not accept order numbers." }}
{{ end }}

{{- /* Whole-order preview by default (suggestFullRefund: true). Pass refundLineItems with
       suggestFullRefund: false to preview specific lines. Shopify ignores refundLineItems when
       suggestFullRefund is true. Money is read in BOTH shop and presentment currencies; refundOrder
       refunds in the presentment currency (what the customer paid). */ -}}
{{- $query := `
query suggestedRefund($orderId: ID!, $refundLineItems: [RefundLineItemInput!], $suggestFullRefund: Boolean, $refundShipping: Boolean, $refundMethodAllocation: RefundMethodAllocation) {
  order(id: $orderId) {
    suggestedRefund(suggestFullRefund: $suggestFullRefund, refundShipping: $refundShipping, refundLineItems: $refundLineItems, refundMethodAllocation: $refundMethodAllocation) {
      amountSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
      maximumRefundableSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
      subtotalSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
      totalTaxSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
      shipping {
        amountSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
        maximumRefundableSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
        taxSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
      }
      refundLineItems { lineItem { id name sku } quantity subtotalSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } } }
      suggestedTransactions {
        parentTransaction { id }
        gateway
        formattedGateway
        accountNumber
        amountSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
      }
    }
  }
}
` -}}

{{- /* suggestFullRefund defaults to true (schema = true). refundShipping is a REQUIRED explicit
       boolean — no silent default: defaulting it on would quietly add a full shipping refund to a
       partial line preview. All shipping (true) or none (false); partial amounts unsupported. */ -}}
{{- $full := true -}}{{- if ne .inputs.suggestFullRefund nil -}}{{- $full = .inputs.suggestFullRefund -}}{{- end -}}
{{ if eq .inputs.refundShipping nil }}{{ stop "refundShipping is required: true to include a full shipping refund, false for none (partial shipping amounts aren't supported)." }}{{ end }}
{{- $ship := .inputs.refundShipping -}}
{{- $alloc := "ORIGINAL_PAYMENT_METHODS" -}}{{- if .inputs.refundMethodAllocation -}}{{- $alloc = .inputs.refundMethodAllocation -}}{{- end -}}

{{- $vars := dict "orderId" .inputs.orderId "suggestFullRefund" $full "refundShipping" $ship "refundMethodAllocation" $alloc -}}
{{- /* refundLineItems is optional on the preview (omit it for a whole-order refund). When given,
       each line carries lineItemId + quantity (both required). */ -}}
{{- if .inputs.refundLineItems -}}
  {{- $lines := list -}}
  {{- range .inputs.refundLineItems -}}
    {{- $lines = append $lines (dict "lineItemId" .lineItemId "quantity" .quantity) -}}
  {{- end -}}
  {{- $_ := set $vars "refundLineItems" $lines -}}
{{- end -}}

{
    "query": {{ toJson $query }},
    "variables": {{ toJson $vars }}
}