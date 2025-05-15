{{- /* Transform subscription data */ -}}
{{- $subscriptions := .rawData.data.Subscriptions -}}
{{- $transformed := list -}}
{{- range $subscriptions -}}
    {{- $subscription := dict 
        "id" .id
        "status" .status
        "createdAt" .createdAt
        "storefrontUserId" .storefrontUserId
        "platformId" .platformId
        "currencyCode" .currencyCode
        "cyclesCompleted" .cyclesCompleted
        "BillingPolicy" .BillingPolicy
        "DeliveryPolicy" .DeliveryPolicy
    -}}
    {{- if .cancelledAt -}}
        {{- $subscription = merge $subscription (dict "cancelledAt" .cancelledAt) -}}
    {{- end -}}
    {{- if .nextBillingDate -}}
        {{- $subscription = merge $subscription (dict "nextBillingDate" .nextBillingDate) -}}
    {{- end -}}
    
    {{- /* Transform lines */ -}}
    {{- $lines := list -}}
    {{- range .SubscriptionLines -}}
        {{- $line := dict 
            "id" .id
            "priceWithoutDiscount" .priceWithoutDiscount
            "quantity" .quantity
            "isPrepaid" .isPrepaid
            "sellingPlanId" .sellingPlanId
            "ProductVariant" .ProductVariant
        -}}
        {{- if .ordersRemaining -}}
            {{- $line = merge $line (dict "ordersRemaining" .ordersRemaining) -}}
        {{- end -}}
        {{- $lines = append $lines $line -}}
    {{- end -}}
    {{- $subscription = merge $subscription (dict "SubscriptionLines" $lines) -}}
    
    {{- $transformed = append $transformed $subscription -}}
{{- end -}}

{{toJson $transformed}}