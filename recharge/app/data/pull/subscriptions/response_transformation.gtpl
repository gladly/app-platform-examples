{{- $hasSubscriptions := and (ne .rawData nil) (ne .rawData.subscriptions nil) (gt (len .rawData.subscriptions) 0) -}}

{{- if $hasSubscriptions -}}
[
    {{- range $index, $subscription := .rawData.subscriptions -}}

        {{- /* create a map with formatted subscription IDs as strings to ensure proper graphql output formatting */ -}}
        {{- $formattedSubscriptionFields := dict 
            "id" ($subscription.id | int64 | toString) 
            "customer_id" ($subscription.customer_id | int64 | toString) 
            "address_id" ($subscription.address_id | int64 | toString) 
        -}}

        {{- /* merge the formatted IDs back into the subscription data */ -}}
        {{- $subscription = mergeOverwrite $subscription $formattedSubscriptionFields -}}

        {{- $subscription | toJson -}}

        {{- if lt (add $index 1) (len $.rawData.subscriptions) -}},{{- end -}}

    {{- end -}}
]
{{- else -}}
[]
{{- end -}}
