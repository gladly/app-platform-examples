{{- $subscriptionId := regexReplaceAll ".*subscription=([^&]+).*" (printf "%s" .request.url) "${1}" -}}

{{- /* Check for API errors first */ -}}
{{- if and .response.statusCode (ne .response.statusCode 200) -}}
    {{- if .rawData.detail -}}
        {{- fail .rawData.detail -}}
    {{- else -}}
        {{- fail "API request failed" -}}
    {{- end -}}
{{- else if and .rawData .rawData.results -}}
{{- /*
     Ordergroove order status codes. All 16 documented codes are mapped here.
     https://developer.ordergroove.com/reference/order-status-codes
     Before 2.3.5 only 7 were mapped and every other code fell through to nil, so
     CANCELLED, MERGED, PENDING_VERIFICATION and the four failure states rendered
     BLANK on the card - i.e. blank in several of the states that generate contacts.
     Codes 19 and 20 were not in the OrderStatus enum at all until 2.3.5.
*/ -}}
{{- $statusNames := dict
      "1"  "UNSENT"
      "3"  "REJECTED"
      "4"  "CANCELLED"
      "5"  "SUCCESS"
      "6"  "SEND_NOW"
      "9"  "PENDING_BATCH_RESPONSE"
      "10" "PENDING_VERIFICATION"
      "11" "PENDING_PLACEMENT"
      "12" "EXCEPTION_DURING_PLACEMENT_PREPARATION"
      "13" "CONNECTION_ERROR_DURING_PLACEMENT"
      "14" "RESPONSE_PROCESSING_ERROR"
      "15" "GENERIC_ERROR_RESPONSE"
      "17" "MERGED"
      "18" "CREDIT_CARD_RETRY"
      "19" "AWAITING_RETRY_INSTRUCTIONS"
      "20" "AWAITING_RESPONSE_CLASSIFICATION" -}}
{{- /* SORT BEFORE RANKING. displayRank and isRecent below are positional, and this
       template used to assume Ordergroove returns orders newest-first. It does not.
       Observed live on a real profile: the card listed Sep 16, then Oct 7, then Sep 13,
       then Aug 13 - a future order sandwiched between two older ones - so the "four most
       recent" were four arbitrary rows and a genuinely recent order could be hidden
       behind an older one. There is no ordering parameter on the endpoint (`ordering`,
       `order_by` and `sort` were all probed live and ignored), so the sort has to happen
       here.

       `place` is "YYYY-MM-DD HH:MM:SS", which sorts correctly as text, so sort on the raw
       value BEFORE the loop normalises it to RFC 3339. Rows with no `place` sort last
       rather than disappearing. */ -}}
{{- $keys := list -}}
{{- $byKey := dict -}}
{{- range $o := .rawData.results -}}
  {{- $key := printf "%s|%s" (default "0000-00-00 00:00:00" $o.place) (default "" $o.public_id) -}}
  {{- $keys = append $keys $key -}}
  {{- $_ := set $byKey $key $o -}}
{{- end -}}
{{- $sorted := list -}}
{{- range $k := reverse (sortAlpha $keys) -}}
  {{- $sorted = append $sorted (get $byKey $k) -}}
{{- end -}}
[
{{- range $i, $order := $sorted -}}
{{- if $i -}},{{- end }}
{{- /* Transform date fields while preserving the rest of the order object */ -}}
{{- if $order.created -}}
    {{- $_ := set $order "created" ($order.created | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}
{{- end -}}
{{- if $order.updated -}}
    {{- $_ := set $order "updated" ($order.updated | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}
{{- end -}}
{{- if $order.place -}}
    {{- $_ := set $order "place" ($order.place | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}
{{- end -}}
{{- /* Ordergroove can return the literal STRING "null" for `cancelled` as well as a
       real JSON null. Normalise both to "not cancelled" ONCE, here, and let every later
       test read $isCancelled - a direct truth test on $order.cancelled is WRONG for the
       string form, because a non-empty string is truthy. isActionable used to do exactly
       that, so an unsent, unlocked, live order whose `cancelled` came back as "null"
       was marked not actionable and disappeared from the card's actionable set and from
       the cancel-order / send-order-early / skip-subscription / issue-discount pickers.
       The string form is also not a valid DateTime, so it is emitted as null. */ -}}
{{- $isCancelled := false -}}
{{- if and $order.cancelled (ne (printf "%v" $order.cancelled) "null") -}}
    {{- $isCancelled = true -}}
    {{- $_ := set $order "cancelled" ($order.cancelled | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}
{{- else -}}
    {{- $_ := set $order "cancelled" nil -}}
{{- end -}}
{{- /* Map numeric status to the OrderStatus enum string, plus a readable twin.
       The card expression language has `title` but no `replace`, so PENDING_PLACEMENT
       would render as "Pending_Placement" if the card did the formatting. */ -}}
{{- $statusName := "" -}}
{{- if $order.status -}}
  {{- $statusName = index $statusNames (printf "%d" (int $order.status)) -}}
  {{- if $statusName -}}
    {{- $_ := set $order "status" $statusName -}}
    {{- $_ := set $order "statusLabel" ($statusName | lower | replace "_" " " | title) -}}
  {{- else -}}
    {{- /* An undocumented code: keep the raw number visible to the agent rather than
           blanking the field, but leave the typed enum null so GraphQL stays valid. */ -}}
    {{- $_ := set $order "statusLabel" (printf "Unknown status (%d)" (int $order.status)) -}}
    {{- $_ := set $order "status" nil -}}
  {{- end -}}
{{- end -}}
{{- /* Compact, one-line summary for the card's collapsed order row. Built here because
       the card expression language has no date formatting and no string building beyond
       `+`, and because a six-line order block times a year of weekly history is exactly
       what made the 2.3.5 card unreadable in a narrow agent pane. */ -}}
{{- $placeLabel := "" -}}
{{- if $order.place -}}
  {{- $placeLabel = ($order.place | toDate "2006-01-02T15:04:05Z" | date "Jan 2, 2006") -}}
  {{- $_ := set $order "placeLabel" $placeLabel -}}
{{- end -}}
{{- $sl := printf "%v" (default "" $order.statusLabel) -}}
{{- if and (ne $placeLabel "") (ne $sl "") -}}
  {{- $_ := set $order "summaryLabel" (printf "%s \u00b7 %s" $placeLabel $sl) -}}
{{- else if ne $placeLabel "" -}}
  {{- $_ := set $order "summaryLabel" $placeLabel -}}
{{- else if ne $sl "" -}}
  {{- $_ := set $order "summaryLabel" $sl -}}
{{- else -}}
  {{- $_ := set $order "summaryLabel" "Order" -}}
{{- end -}}
{{- /* Ordergroove returns orders newest-first - verified live, there is no ordering
       param - so index 0 is the most recent. The card renders only the first few rows
       by default; the rest stay in the payload for actions and order pickers. This is a
       DISPLAY cap and is unrelated to the CARD-7 fan-out cap, which bounds REQUESTS. */ -}}
{{- $_ := set $order "displayRank" $i -}}
{{- /* Add subscription info */ -}}
{{- if not $order.subscription -}}
    {{- if .request.url -}}
        {{- $_ := set $order "subscription" $subscriptionId -}}
    {{- end -}}
{{- end -}}
{{- $_ := set $order "subscriptionId" $subscriptionId -}}
{{- /* Card-safe camelCase twins. The flexible.card XSD forbids underscores in
       dataSource refs and the platform does not camelCase these at the card layer. */ -}}
{{- $_ := set $order "publicId" $order.public_id -}}
{{- $_ := set $order "subTotal" $order.sub_total -}}
{{- $_ := set $order "taxTotal" $order.tax_total -}}
{{- $_ := set $order "shippingTotal" $order.shipping_total -}}
{{- $_ := set $order "discountTotal" $order.discount_total -}}
{{- $_ := set $order "currencyCode" $order.currency_code -}}
{{- $_ := set $order "orderMerchantId" $order.order_merchant_id -}}
{{- /* Ordergroove returns rejected_message as a JSON-ENCODED STRING, not prose:
       {"code": "500", "message": "Expiration Date is in the past."}
       Bound straight to the card that renders as a raw JSON blob in the agent pane
       (observed live on a sandbox profile). The card expression language has no
       `replace` and cannot parse JSON, so the readable message has to be extracted
       here. Key order varies between rows, so match on content, not position, and
       fall back to the raw value for anything that is not a JSON object carrying a
       message. */ -}}
{{- $rejected := printf "%v" (default "" $order.rejected_message) -}}
{{- if and (hasPrefix "{" $rejected) (contains "\"message\"" $rejected) -}}
  {{- $parsed := fromJson $rejected -}}
  {{- if and $parsed $parsed.message -}}
    {{- $_ := set $order "rejectedMessage" (printf "%v" $parsed.message) -}}
  {{- else -}}
    {{- $_ := set $order "rejectedMessage" $rejected -}}
  {{- end -}}
{{- else -}}
  {{- $_ := set $order "rejectedMessage" $order.rejected_message -}}
{{- end -}}
{{- $_ := set $order "shippingAddress" $order.shipping_address -}}
{{- /* Int fields render as "2.00" through a NumericValue binding, so give the card a String. */ -}}
{{- if ne $order.tries nil -}}
  {{- $_ := set $order "triesLabel" (printf "%d" (int $order.tries)) -}}
{{- end -}}
{{- /* An order is actionable only while it is still unsent, unlocked and not cancelled.
       Drives which order-level actions the card offers and which orders the pickers list. */ -}}
{{- $_ := set $order "isActionable" (and (eq $statusName "UNSENT") (ne (printf "%v" $order.locked) "true") (not $isCancelled)) -}}
{{- /* Which orders the card renders by default. MUST stay after isActionable is set -
       reading it earlier yields nil and silently degrades to the index test alone.
       Live render showed Ordergroove's default order is NOT strictly by placement date
       (a future UNSENT order came back at index 1, between two placed ones), so a fixed
       top-N alone can hide the one order an agent needs. Every actionable order is
       therefore always shown, plus the four most recent. */ -}}
{{- $_ := set $order "isRecent" (or $order.isActionable (lt $i 4)) -}}
{{toJson $order}}
{{- end -}}
]
{{- else -}}
[]
{{- end -}}
