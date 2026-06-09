{{- $subs := list -}}

{{- if and (kindIs "map" .rawData) .rawData.data -}}
    {{- range $subscription := .rawData.data -}}

        {{- /* derive a human-readable billing cadence, e.g. "Every 2 months" */ -}}
        {{- $cadence := "" -}}
        {{- if and $subscription.orderIntervalFrequency $subscription.orderIntervalUnit -}}
            {{- $freq := $subscription.orderIntervalFrequency | int -}}
            {{- $unit := $subscription.orderIntervalUnit | toString | lower -}}
            {{- if gt $freq 1 -}}
                {{- $cadence = printf "Every %d %ss" $freq $unit -}}
            {{- else -}}
                {{- $cadence = printf "Every %s" $unit -}}
            {{- end -}}
        {{- end -}}

        {{- /* map line items; ID-ish fields are set only when present so absent stays null,
               and numeric IDs (decoded as float64) normalize to strings */ -}}
        {{- $lineItems := list -}}
        {{- range $line := $subscription.lineItems -}}
            {{- $entry := dict
                "quantity" $line.quantity
                "unitPrice" $line.unitPrice
                "subtotalPrice" $line.subtotalPrice
            -}}
            {{- if ne $line.lineId nil -}}
                {{- $_ := set $entry "lineId" (kindIs "float64" $line.lineId | ternary ($line.lineId | int64 | toString) ($line.lineId | toString)) -}}
            {{- end -}}
            {{- if ne $line.shopifyProductId nil -}}
                {{- $_ := set $entry "shopifyProductId" (kindIs "float64" $line.shopifyProductId | ternary ($line.shopifyProductId | int64 | toString) ($line.shopifyProductId | toString)) -}}
            {{- end -}}
            {{- if ne $line.shopifyVariantId nil -}}
                {{- $_ := set $entry "shopifyVariantId" (kindIs "float64" $line.shopifyVariantId | ternary ($line.shopifyVariantId | int64 | toString) ($line.shopifyVariantId | toString)) -}}
            {{- end -}}
            {{- $lineItems = append $lineItems $entry -}}
        {{- end -}}

        {{- $item := dict
            "status" $subscription.status
            "createdAt" $subscription.createdAt
            "updatedAt" (coalesce $subscription.updatedAt $subscription.createdAt)
            "nextBillingDate" $subscription.nextBillingDate
            "pausedUntil" $subscription.pausedUntil
            "orderNotes" $subscription.orderNotes
            "price" $subscription.price
            "deliveryPrice" $subscription.deliveryPrice
            "currency" $subscription.currency
            "churnedAt" $subscription.churnedAt
            "cancelledAt" $subscription.cancelledAt
            "cancellationReason" $subscription.cancellationReason
            "orderIntervalFrequency" $subscription.orderIntervalFrequency
            "orderIntervalUnit" $subscription.orderIntervalUnit
            "billingCadence" $cadence
            "lineItems" $lineItems
        -}}
        {{- if ne $subscription.id nil -}}
            {{- $_ := set $item "id" (kindIs "float64" $subscription.id | ternary ($subscription.id | int64 | toString) ($subscription.id | toString)) -}}
        {{- end -}}
        {{- if ne $subscription.subscriptionId nil -}}
            {{- $_ := set $item "subscriptionId" (kindIs "float64" $subscription.subscriptionId | ternary ($subscription.subscriptionId | int64 | toString) ($subscription.subscriptionId | toString)) -}}
        {{- end -}}

        {{- $subs = append $subs $item -}}
    {{- end -}}
{{- end -}}

{{- $subs | toJson -}}
