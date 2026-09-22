{{- $isSuccess := and (ge (int .response.statusCode) 200) (lt (int .response.statusCode) 300) -}}

{{- /* This lookup returns a bare nullable Subscription with no error envelope, so any
       unexpected non-2xx must fail rather than fall through to null — a null on a 5xx would
       look like "no such subscription" to Gladly AI during a Recharge outage. Legitimate
       not-found is a 2xx with an empty list, handled below. (401/403 are runtime-handled.) */ -}}
{{- if not $isSuccess -}}
    {{- fail (printf "Recharge returned an unexpected status code: %d" (int .response.statusCode)) -}}
{{- end -}}

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
