{{- $hasSubscriptions := and (ne .rawData nil) (ne .rawData.subscriptions nil) (gt (len .rawData.subscriptions) 0) -}}


{{- if $hasSubscriptions -}}
    {{- $subscription := index .rawData.subscriptions 0 -}}

    {{- /* create a map with formatted subscription IDs as strings to ensure proper graphql output formatting */ -}}
    {{- $formattedSubscriptionFields := dict 
        "id" ($subscription.id | int64 | toString) 
        "customer_id" ($subscription.customer_id | int64 | toString) 
        "address_id" ($subscription.address_id | int64 | toString) 
    -}}

    {{- /* merge the formatted IDs back into the subscription data */ -}}
    {{- $subscription = mergeOverwrite $subscription $formattedSubscriptionFields -}}

    {{- $subscription | toJson -}}

{{- else -}}
    null
{{- end -}}
