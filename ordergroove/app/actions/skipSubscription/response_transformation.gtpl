{{- if and .response (ne .response.statusCode 200) -}}
  {{- if eq .response.statusCode 404 -}}
    {{ stop (printf "order with id %s does not exist" .inputs.orderId) }}
  {{- end -}}
  {{- if eq .response.statusCode 400 -}}
    {{- if and .rawData.subscription -}}
      {{- if gt (len .rawData.subscription) 0 -}}
      {{- $messages := "" -}}
      {{- range $i, $msg := .rawData.subscription -}}
        {{- if $i -}}, {{- end -}}
        {{- $messages = printf "%s%s" $messages $msg -}}
      {{- end -}}
      {{ stop (printf "skip subscription failed: %s" $messages) }}
      {{- end -}}
    {{- end -}}
  {{- end -}}
  {{ fail (printf "skip subscription failed: %d %s" .response.statusCode .response.body) }}
{{- end -}}
{{- $data := dict -}}
{{- range $key, $value := .rawData -}}
  {{- if eq $key "created" -}}
    {{- if $value -}}
      {{- $data = set $data $key (date "2006-01-02T15:04:05Z" (toDate "2006-01-02 15:04:05" $value)) -}}
    {{- else -}}
      {{- $data = set $data $key nil -}}
    {{- end -}}
  {{- else if eq $key "place" -}}
    {{- if $value -}}
      {{- $data = set $data $key (date "2006-01-02T15:04:05Z" (toDate "2006-01-02 15:04:05" $value)) -}}
    {{- else -}}
      {{- $data = set $data $key nil -}}
    {{- end -}}
  {{- else if eq $key "cancelled" -}}
    {{- if and $value (ne $value "null") -}}
      {{- $data = set $data $key (date "2006-01-02T15:04:05Z" (toDate "2006-01-02 15:04:05" $value)) -}}
    {{- else -}}
      {{- $data = set $data $key nil -}}
    {{- end -}}
  {{- else if eq $key "status" -}}
    {{- /* All 16 documented Ordergroove order status codes.
           https://developer.ordergroove.com/reference/order-status-codes
           Before 2.3.5 only 7 were mapped here and every other code fell through to
           nil - CANCELLED, MERGED, PENDING_VERIFICATION and the four failure states
           all rendered blank. This map is duplicated in data/pull/orders, cancelOrder
           and sendOrderNow because App Platform templates cannot be shared.
           ../../../scripts/check_status_map.sh (run in CI by the "Run app
           consistency checks" step, and locally by `make check-status-map`, which
           `make test` depends on) extracts the $statusNames dict from all four
           templates and fails if any copy is missing a code the others have, maps a
           code to a different name, or disagrees in count. Adding a code means
           editing all four. */ -}}
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
    {{- $name := index $statusNames (printf "%d" (int $value)) -}}
    {{- if $name -}}
      {{- $data = set $data $key $name -}}
      {{- $data = set $data "statusLabel" ($name | lower | replace "_" " " | title) -}}
    {{- else -}}
      {{- $data = set $data $key nil -}}
      {{- $data = set $data "statusLabel" (printf "Unknown status (%d)" (int $value)) -}}
    {{- end -}}
  {{- else -}}
    {{- $data = set $data $key $value -}}
  {{- end -}}
{{- end -}}
{{- /* Card-safe camelCase twins, matching the orders data pull. */ -}}
{{- $data = set $data "publicId" (get $data "public_id") -}}
{{- $data = set $data "subTotal" (get $data "sub_total") -}}
{{- $data = set $data "currencyCode" (get $data "currency_code") -}}
{{- if ne (get $data "tries") nil -}}
  {{- $data = set $data "triesLabel" (printf "%d" (int (get $data "tries"))) -}}
{{- end -}}
{{toJson $data}}
