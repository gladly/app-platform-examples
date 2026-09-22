{{- $hasSubscriptions := and (ne .rawData nil) (ne .rawData.subscriptions nil) (gt (len .rawData.subscriptions) 0) -}}

{{- /* The Delay next order form can't read integration.configuration, so echo the admin's
       maximum-delay cap (in billing cycles, default 3) onto each subscription here. */ -}}
{{- $maxDelayCycles := .integration.configuration.maxDelayCycles | default 3 | int -}}

{{- if $hasSubscriptions -}}
    {{- /* Build each subscription's final record first, then sort active-first (stable within each
           group) so forms/cards act on the subscriptions that matter without cancelled ones diluting
           them -- the card can't sort itself. */ -}}
    {{- $active := list -}}
    {{- $other := list -}}
    {{- range $index, $subscription := .rawData.subscriptions -}}

        {{- /* create a map with formatted subscription IDs as strings to ensure proper graphql output formatting */ -}}
        {{- $formattedSubscriptionFields := dict
            "id" ($subscription.id | int64 | toString)
            "customer_id" ($subscription.customer_id | int64 | toString)
            "address_id" ($subscription.address_id | int64 | toString)
        -}}

        {{- /* camelCase aliases for the profile card: the flexible.card XSD forbids underscores in
               dataSource refs and the platform does not camelCase snake_case fields at the card layer,
               so multi-word fields need an explicit camelCase twin. Declared on the Subscription data
               type; mirror the snake_case fields 1:1. */ -}}
        {{- $cardAliases := dict
            "productTitle" $subscription.product_title
            "nextChargeScheduledAt" $subscription.next_charge_scheduled_at
            "chargeIntervalFrequency" $subscription.charge_interval_frequency
            "orderIntervalFrequency" $subscription.order_interval_frequency
            "orderIntervalUnit" $subscription.order_interval_unit
            "presentmentCurrency" $subscription.presentment_currency
            "variantTitle" $subscription.variant_title
            "hasQueuedCharges" $subscription.has_queued_charges
            "maxRetriesReached" $subscription.max_retries_reached
            "createdAt" $subscription.created_at
            "updatedAt" $subscription.updated_at
            "cancelledAt" $subscription.cancelled_at
            "cancellationReason" $subscription.cancellation_reason
        -}}

        {{- $formSettings := dict "maxDelayCycles" $maxDelayCycles -}}

        {{- /* merge the formatted IDs, card aliases, and form settings back into the subscription data */ -}}
        {{- $subscription = mergeOverwrite $subscription $formattedSubscriptionFields $cardAliases $formSettings -}}

        {{- if eq ($subscription.status | toString) "active" -}}
            {{- $active = append $active $subscription -}}
        {{- else -}}
            {{- $other = append $other $subscription -}}
        {{- end -}}

    {{- end -}}

    {{- $sorted := list -}}
    {{- range $active}}{{- $sorted = append $sorted .}}{{- end -}}
    {{- range $other}}{{- $sorted = append $sorted .}}{{- end -}}

[
    {{- range $index, $subscription := $sorted -}}
        {{- $subscription | toJson -}}
        {{- if lt (add $index 1) (len $sorted) -}},{{- end -}}
    {{- end -}}
]
{{- else -}}
[]
{{- end -}}
