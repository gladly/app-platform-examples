{{- /* Check for API errors first */ -}}
{{- if and .response.statusCode (ne .response.statusCode 200) -}}
    {{- if .rawData.detail -}}
        {{- fail .rawData.detail -}}
    {{- else -}}
        {{- fail "API request failed" -}}
    {{- end -}}
{{- else if and .rawData .rawData.results -}}
{{- /* every_period is a code, not a unit: 1=days, 2=weeks, 3=months, 4=years. */ -}}
{{- $periodSingular := dict "1" "day" "2" "week" "3" "month" "4" "year" -}}
{{- $periodPlural := dict "1" "days" "2" "weeks" "3" "months" "4" "years" -}}
[
{{- range $index, $subscription := .rawData.results -}}
{{- if $index -}},{{- end }}
{{- /* Transform date fields while preserving the rest of the subscription object */ -}}
{{- if $subscription.start_date -}}
    {{- $_ := set $subscription "start_date" ($subscription.start_date | toDate "2006-01-02" | date "2006-01-02T15:04:05Z") -}}
{{- end -}}
{{- if $subscription.cancelled -}}
    {{- $_ := set $subscription "cancelled" ($subscription.cancelled | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}
{{- end -}}
{{- if $subscription.created -}}
    {{- $_ := set $subscription "created" ($subscription.created | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}
{{- end -}}
{{- if $subscription.updated -}}
    {{- $_ := set $subscription "updated" ($subscription.updated | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}
{{- end -}}
{{- /* Card-safe camelCase twins. The flexible.card XSD forbids underscores in
       dataSource refs and the platform does not camelCase these at the card layer. */ -}}
{{- $_ := set $subscription "publicId" $subscription.public_id -}}
{{- $_ := set $subscription "subscriptionType" $subscription.subscription_type -}}
{{- $_ := set $subscription "frequencyDays" $subscription.frequency_days -}}
{{- $_ := set $subscription "everyPeriod" $subscription.every_period -}}
{{- $_ := set $subscription "startDate" $subscription.start_date -}}
{{- $_ := set $subscription "cancelReason" $subscription.cancel_reason -}}
{{- $_ := set $subscription "cancelReasonCode" $subscription.cancel_reason_code -}}
{{- $_ := set $subscription "currencyCode" $subscription.currency_code -}}
{{- $_ := set $subscription "merchantOrderId" $subscription.merchant_order_id -}}
{{- $_ := set $subscription "shippingAddress" $subscription.shipping_address -}}
{{- $_ := set $subscription "productAttribute" $subscription.product_attribute -}}
{{- /* Title fallback for when the product is missing from the products pull. The card
       expression language's `+` is NUMERIC, so building "Subscription " + publicId there
       renders NaN - compose the finished string here instead. The product NAME cannot be
       resolved in this pull: the products pull declares
       dependsOnDataTypes ["ordergroove_item", "ordergroove_subscription"], so it runs
       AFTER this one. The card keeps its productDetail null-check and binds this only as
       the else branch. */ -}}
{{- if $subscription.public_id -}}
  {{- $_ := set $subscription "fallbackTitleLabel" (printf "Subscription %v" $subscription.public_id) -}}
{{- end -}}
{{- /* Int renders as "2.00" through a NumericValue binding, so give the card a String. */ -}}
{{- if ne $subscription.quantity nil -}}
  {{- $_ := set $subscription "quantityLabel" (printf "%d" (int $subscription.quantity)) -}}
{{- end -}}
{{- /* Readable state. `live` is the authoritative flag; `cancelled` is a date or null. */ -}}
{{- if $subscription.cancelled -}}
  {{- $_ := set $subscription "statusLabel" "Cancelled" -}}
{{- else if eq (printf "%v" $subscription.live) "true" -}}
  {{- $_ := set $subscription "statusLabel" "Active" -}}
{{- else -}}
  {{- $_ := set $subscription "statusLabel" "Inactive" -}}
{{- end -}}
{{- /* Readable cadence, e.g. "Every 2 months" / "Every month". */ -}}
{{- if and (ne $subscription.every nil) (ne $subscription.every_period nil) -}}
  {{- $n := int $subscription.every -}}
  {{- $pkey := printf "%d" (int $subscription.every_period) -}}
  {{- if eq $n 1 -}}
    {{- $unit := index $periodSingular $pkey -}}
    {{- if $unit -}}
      {{- $_ := set $subscription "cadenceLabel" (printf "Every %s" $unit) -}}
    {{- end -}}
  {{- else -}}
    {{- $unit := index $periodPlural $pkey -}}
    {{- if $unit -}}
      {{- $_ := set $subscription "cadenceLabel" (printf "Every %d %s" $n $unit) -}}
    {{- end -}}
  {{- end -}}
{{- end -}}
{{- /* Flatten prepaid_subscription_context into scalars.
       This field is declared String on the data type and a structured object on the
       actions type. The card must NEVER bind into it: a null nested field blanks the
       ENTIRE card with "Something's wrong with this card." kindIs guards the access.
       These scalars also gate changeSubscriptionQuantity and changeItemQuantity, both
       of which return 400 on a prepaid subscription. */ -}}
{{- $ctx := $subscription.prepaid_subscription_context -}}
{{- if kindIs "map" $ctx -}}
  {{- $_ := set $subscription "isPrepaid" true -}}
  {{- if ne (get $ctx "prepaid_orders_remaining") nil -}}
    {{- $remaining := printf "%d" (int (get $ctx "prepaid_orders_remaining")) -}}
    {{- $_ := set $subscription "prepaidOrdersRemaining" $remaining -}}
    {{- /* Finished display string - the card's `+` is numeric and would render NaN. */ -}}
    {{- $_ := set $subscription "prepaidLabel" (printf "%s shipment(s) left" $remaining) -}}
  {{- end -}}
  {{- $rb := get $ctx "renewal_behavior" -}}
  {{- if $rb -}}
    {{- $_ := set $subscription "renewalBehaviorLabel" ($rb | toString | lower | replace "_" " " | title) -}}
  {{- end -}}
{{- else -}}
  {{- $_ := set $subscription "isPrepaid" false -}}
{{- end -}}
{{toJson $subscription}}
{{- end }}
]
{{- else -}}
[]
{{- end -}}
