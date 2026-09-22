{{- /* refundCreate, idempotent per execution: the @idempotent(key:) directive (mandatory as of
       2026-04) uses the platform request correlationId as the key, so a retried execution replays
       the original refund instead of duplicating it — there is no caller-supplied key.
       Non-obvious choices below:
       - `currency` MUST be the order's presentment currency, or Shopify rejects the refund.
       - kind: REFUND only refunds CAPTURED payments; voiding an uncaptured authorization is a
         cancellation (cancel_order), not a refund.
       - restockType: NO_RESTOCK — this action moves money only, it does not restock inventory. */ -}}
{{ if not (regexMatch `^gid://shopify/Order/[0-9]+$` (.inputs.orderId | toString)) }}
	{{ stop "orderId must be a Shopify Order GID like 'gid://shopify/Order/1234567890'." }}
{{ end }}
{{ if or (eq .inputs.currencyCode nil) (eq .inputs.currencyCode "") }}
	{{ stop "currencyCode is required (the order's presentment currency from suggestedRefund, e.g. \"USD\")." }}
{{ end }}
{{ if eq .inputs.refundShipping nil }}
	{{ stop "refundShipping is required: it must match the value used in the suggestedRefund preview (true refunds all shipping, false none). Partial shipping amounts aren't supported." }}
{{ end }}
{{ if or (eq .correlationId nil) (eq .correlationId "") }}
	{{ fail "correlationId is missing from the request context; it is required as the refund idempotency key." }}
{{ end }}
{{- $method := "ORIGINAL_PAYMENT_METHODS" -}}{{- if .inputs.refundMethod -}}{{- $method = .inputs.refundMethod -}}{{- end -}}
{{ if eq $method "STORE_CREDIT" }}
	{{ if or (eq .inputs.storeCreditAmount nil) (eq (.inputs.storeCreditAmount | toString) "") }}
		{{ stop "storeCreditAmount is required when refundMethod is STORE_CREDIT (the presentment amount to issue as store credit, from the suggestedRefund preview's amountSet)." }}
	{{ else if le (float64 .inputs.storeCreditAmount) 0.0 }}
		{{ stop "Nothing to refund: the store-credit amount is 0. An unpaid or unfulfilled order has nothing to refund and should be cancelled via cancel_order instead." }}
	{{ end }}
{{ else }}
	{{ if not .inputs.transactions }}
		{{ stop "transactions is required when refunding to the original payment method (copy suggestedTransactions from a suggestedRefund preview)." }}
	{{ end }}
{{ end }}

{{- /* Fetch back enough to confirm the refund without a second lookup: the refund detail (its line
       items + the transactions that moved the money) and a compact order overview with the order's
       post-refund financial state. refundLineItems / transactions are connections — flatten the
       `nodes` in the transformation, mirroring order_by_identifier's refunds mapping. */ -}}
{{- $query := `
mutation refundOrder($input: RefundInput!, $key: String!) {
  refundCreate(input: $input) @idempotent(key: $key) {
    refund {
      id
      createdAt
      note
      totalRefundedSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
      refundLineItems(first: 50) { nodes { quantity restockType lineItem { id } } }
      transactions(first: 10) { nodes { id kind status gateway formattedGateway accountNumber amountSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } } } }
      order {
        id
        name
        displayFinancialStatus
        totalRefundedSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
      }
    }
    userErrors { field message }
  }
}
` -}}

{{- /* refundShipping is required (validated above) — read it directly, no silent default. */ -}}
{{- $ship := .inputs.refundShipping -}}
{{- $notify := false -}}{{- if ne .inputs.notify nil -}}{{- $notify = .inputs.notify -}}{{- end -}}

{{- $rlis := list -}}
{{- range .inputs.refundLineItems -}}
  {{- $rlis = append $rlis (dict "lineItemId" .lineItemId "quantity" .quantity "restockType" "NO_RESTOCK") -}}
{{- end -}}

{{- /* Build the money-movement instruction by allocation:
       ORIGINAL_PAYMENT_METHODS → `transactions` (kind REFUND) back to the original payment;
       STORE_CREDIT → empty `transactions` + a `storeCreditRefund` refundMethod (Shopify routes it
       through the shopify_store_credit gateway). GROUND-TRUTHED on the dev store: store credit
       issues via refundMethods.storeCreditRefund.amount in the presentment currency. */ -}}
{{- $txns := list -}}
{{- $refundMethods := list -}}
{{- if eq $method "STORE_CREDIT" -}}
  {{- $sc := dict "amount" (dict "amount" ($.inputs.storeCreditAmount | toString) "currencyCode" $.inputs.currencyCode) -}}
  {{- if .inputs.storeCreditExpiresAt -}}{{- $_ := set $sc "expiresAt" .inputs.storeCreditExpiresAt -}}{{- end -}}
  {{- $refundMethods = append $refundMethods (dict "storeCreditRefund" $sc) -}}
{{- else -}}
  {{- range .inputs.transactions -}}
    {{- $txns = append $txns (dict "orderId" $.inputs.orderId "gateway" .gateway "kind" "REFUND" "amount" (.amount | toString) "parentId" .parentTransactionId) -}}
  {{- end -}}
{{- end -}}

{{- $input := dict
    "orderId" .inputs.orderId
    "currency" .inputs.currencyCode
    "notify" $notify
    "shipping" (dict "fullRefund" $ship)
    "refundLineItems" $rlis
    "transactions" $txns -}}
{{- if $refundMethods -}}{{- $_ := set $input "refundMethods" $refundMethods -}}{{- end -}}
{{- if .inputs.note -}}{{- $_ := set $input "note" .inputs.note -}}{{- end -}}

{
    "query": {{ toJson $query }},
    "variables": {{ toJson (dict "input" $input "key" .correlationId) }}
}