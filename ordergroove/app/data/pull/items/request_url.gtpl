{{- /*
     CARD-7 fan-out mitigation.

     This pull issues ONE request per order, and the orders pull issues one request per
     subscription with no date or status filter -- it returns the customer's entire order
     history. An ULTA-scale replenishment customer with 5 subscriptions and two years of
     history is therefore ~500 sequential item requests on every profile open, which
     shows up live as a card that never finishes or trips the payload cap. Nothing
     offline catches it.

     Until Ordergroove confirms a date/status filter on GET /orders/ (SCOPING.md §7
     row 2 -- the real fix), bound the fan-out here.

     THE LIST IS CROSS-SUBSCRIPTION. `.externalData.ordergroove_order` is NOT one
     subscription's orders: the orders pull emits one request per subscription and the
     platform concatenates every response into a single flat array under the data-type
     name. A cap applied to that flat array takes the head of the CONCATENATION, so a
     customer with subscription A (12 completed orders, returned first) and subscription
     B (3) got items for 10 of A's orders and none of B's -- while the orders transform
     still marked B's newest four `isRecent`, so the card rendered those rows with empty
     line-item lists. The cap is therefore applied PER SUBSCRIPTION, keyed on the
     order's `subscriptionId` (set unconditionally by the orders transform and declared
     String! on the Order type).

     Two buckets:
       - LIVE orders are always fetched, uncapped. These are the ones an agent acts on
         and they do not accumulate: a subscription has one upcoming order plus at most
         one in flight or actively retrying.
       - TERMINAL orders are history. Keep the newest 6 per subscription, 30 overall.

     Bucketing is an allowlist of live statuses, so an unmapped/nil status falls to the
     capped side and can never reopen the unbounded fan-out. Terminality is taken from
     the status codes' own documentation (data_schema.graphql, OrderStatus):
       - REJECTED ("after the retry limit has been reached") and RESPONSE_PROCESSING_ERROR
         ("this order is not retried") are terminal failures, as is
         EXCEPTION_DURING_PLACEMENT_PREPARATION (nothing retries it). They are capped
         with the rest of history. This matters: a subscriber whose card has declined
         every cycle for two years accumulates ~100 REJECTED orders, so leaving failures
         in the uncapped bucket left the fan-out unbounded for exactly the billing-failure
         customers who generate the most contacts.
       - GENERIC_ERROR_RESPONSE ("merchant sets number of times to retry") and
         CONNECTION_ERROR_DURING_PLACEMENT ("may be retried ... no retry limit") still
         progress on their own, and AWAITING_RETRY_INSTRUCTIONS is waiting on a human.
         Those stay uncapped: they are short-lived and they are what an agent is being
         asked about.

     Worst case with these numbers: 5 subscriptions x 6 historical = 30 item requests,
     plus ~2 live orders per subscription = ~10, so ~40 requests instead of ~520. The
     30-request history backstop is filled ROUND-ROBIN (each subscription's newest
     order first, then each subscription's second-newest, ...), so a customer with more
     subscriptions than the backstop can cover loses depth evenly rather than losing
     whole subscriptions off the end of the list.

     Ordergroove returns orders NEWEST-FIRST and supports no ordering param (verified
     live 2026-09-15: `ordering`, `order_by` and `sort` all matched a bogus-param
     control). So the newest terminal orders are the FIRST rows, not the last.
     An earlier cut of this template took the last N and kept the OLDEST orders --
     exactly backwards. Nothing offline caught it; only a live pull did.
*/ -}}
{{- if and .externalData.ordergroove_order (gt (len .externalData.ordergroove_order) 0) -}}
{{- $historyPerSubscription := 6 -}}
{{- $historyTotalLimit := 30 -}}
{{- $liveStatuses := dict
      "UNSENT" true
      "SEND_NOW" true
      "PENDING_BATCH_RESPONSE" true
      "PENDING_VERIFICATION" true
      "PENDING_PLACEMENT" true
      "CONNECTION_ERROR_DURING_PLACEMENT" true
      "GENERIC_ERROR_RESPONSE" true
      "CREDIT_CARD_RETRY" true
      "AWAITING_RETRY_INSTRUCTIONS" true
      "AWAITING_RESPONSE_CLASSIFICATION" true -}}
{{- $live := list -}}
{{- $subscriptionIds := list -}}
{{- $historyBySubscription := dict -}}
{{- range $o := .externalData.ordergroove_order -}}
  {{- $s := printf "%v" $o.status -}}
  {{- if index $liveStatuses $s -}}
    {{- $live = append $live $o -}}
  {{- else -}}
    {{- /* Group history by parent subscription so each one keeps its own recent orders. */ -}}
    {{- $sub := printf "%v" (default "" $o.subscriptionId) -}}
    {{- if not (hasKey $historyBySubscription $sub) -}}
      {{- $subscriptionIds = append $subscriptionIds $sub -}}
      {{- $_ := set $historyBySubscription $sub list -}}
    {{- end -}}
    {{- $kept := index $historyBySubscription $sub -}}
    {{- if lt (len $kept) $historyPerSubscription -}}
      {{- /* newest-first, so keeping the head of the list keeps the most recent */ -}}
      {{- $_ := set $historyBySubscription $sub (append $kept $o) -}}
    {{- end -}}
  {{- end -}}
{{- end -}}
{{- $selected := $live -}}
{{- $historyCount := 0 -}}
{{- range $rank := until $historyPerSubscription -}}
  {{- range $sub := $subscriptionIds -}}
    {{- $kept := index $historyBySubscription $sub -}}
    {{- if and (lt $rank (len $kept)) (lt $historyCount $historyTotalLimit) -}}
      {{- $selected = append $selected (index $kept $rank) -}}
      {{- $historyCount = add1 $historyCount -}}
    {{- end -}}
  {{- end -}}
{{- end -}}
{{- range $selected -}}
https://restapi.ordergroove.com/items/?order={{urlquery .public_id}}&page_size=100
{{ end -}}
{{- end -}}
