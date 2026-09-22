{{- /* Error handling. 404 = unknown order; 400 = validation failure, either
       {"detail": "..."} or a per-field map of message lists. */ -}}
{{- if and .response (ne .response.statusCode 200) -}}
  {{- if eq .response.statusCode 404 -}}
    {{ stop (printf "Order %s does not exist in Ordergroove." .inputs.orderId) }}
  {{- end -}}
  {{- if eq .response.statusCode 400 -}}
    {{- if .rawData.detail -}}
      {{ stop (printf "Place order early: %s" .rawData.detail) }}
    {{- end -}}
    {{- range $field, $msgs := .rawData -}}
      {{- if kindIs "slice" $msgs -}}
        {{- if gt (len $msgs) 0 -}}
          {{ stop (printf "Place order early: %s" (join ", " $msgs)) }}
        {{- end -}}
      {{- end -}}
    {{- end -}}
    {{ stop "Place order early: Ordergroove rejected the change." }}
  {{- end -}}
  {{- /* Orders churn continuously, and `isActionable` is computed when the pull runs, so
         an order can leave UNSENT between the card loading and the agent acting. See the
         comment in deleteOrderItem/response_transformation.gtpl. */ -}}
  {{- if ge .response.statusCode 500 -}}
    {{ stop "Place order early: Ordergroove would not change this order. It is usually because the order is no longer open - it may have been placed, rejected or skipped since this card was loaded. Refresh the customer's profile to see the current orders, then try again." }}
  {{- end -}}
  {{ fail (printf "Place order early failed: %d %s" .response.statusCode .response.body) }}
{{- end -}}
{{- /* All 16 documented Ordergroove order status codes.
       https://developer.ordergroove.com/reference/order-status-codes
       This map is duplicated in data/pull/orders, skipSubscription, cancelOrder and
       sendOrderNow because App Platform templates cannot be shared. Keep every copy
       identical: appcfg never renders cards, so a stale copy only shows up live, as
       "Unknown status (N)" in the agent pane.
       ../../../scripts/check_status_map.sh (run in CI by the "Run app consistency
       checks" step, and locally by `make check-status-map`, which `make test`
       depends on) extracts the $statusNames dict from all four templates
       and fails if any copy is missing a code the others have, maps a code to a
       different name, or disagrees in count. Adding a code means editing all four. */ -}}
{{- $statusNames := dict
      "1"  "UNSENT"           "3"  "REJECTED"
      "4"  "CANCELLED"        "5"  "SUCCESS"
      "6"  "SEND_NOW"         "9"  "PENDING_BATCH_RESPONSE"
      "10" "PENDING_VERIFICATION"
      "11" "PENDING_PLACEMENT"
      "12" "EXCEPTION_DURING_PLACEMENT_PREPARATION"
      "13" "CONNECTION_ERROR_DURING_PLACEMENT"
      "14" "RESPONSE_PROCESSING_ERROR"
      "15" "GENERIC_ERROR_RESPONSE"
      "17" "MERGED"           "18" "CREDIT_CARD_RETRY"
      "19" "AWAITING_RETRY_INSTRUCTIONS"
      "20" "AWAITING_RESPONSE_CLASSIFICATION" -}}
{{- $o := .rawData -}}
{{- if $o.created -}}{{- $_ := set $o "created" ($o.created | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}{{- end -}}
{{- if $o.place -}}{{- $_ := set $o "place" ($o.place | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}{{- end -}}
{{- if and $o.cancelled (ne (printf "%v" $o.cancelled) "null") -}}{{- $_ := set $o "cancelled" ($o.cancelled | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}{{- end -}}
{{- if $o.status -}}
  {{- $name := index $statusNames (printf "%d" (int $o.status)) -}}
  {{- if $name -}}
    {{- $_ := set $o "status" $name -}}
    {{- $_ := set $o "statusLabel" ($name | lower | replace "_" " " | title) -}}
  {{- else -}}
    {{- $_ := set $o "statusLabel" (printf "Unknown status (%d)" (int $o.status)) -}}
    {{- $_ := set $o "status" nil -}}
  {{- end -}}
{{- end -}}
{{- $_ := set $o "publicId" $o.public_id -}}
{{- $_ := set $o "subTotal" $o.sub_total -}}
{{- $_ := set $o "currencyCode" $o.currency_code -}}
{{- if ne $o.tries nil -}}{{- $_ := set $o "triesLabel" (printf "%d" (int $o.tries)) -}}{{- end -}}
{{toJson $o}}
