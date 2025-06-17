{{- $isSuccess := eq .response.statusCode 200 -}}

{{- $subscription := .rawData.subscription -}}

{{- /* create a map with formatted subscription IDs as strings to ensure proper graphql output formatting */}}
{{- $formattedSubscriptionIds := dict 
        "id" ($subscription.id | int64 | toString) 
        "customer_id" ($subscription.customer_id | int64 | toString) 
        "address_id" ($subscription.address_id | int64 | toString) 
-}}

{{- /* merge the formatted IDs back into the subscription data */}}
{{- $subscription = mergeOverwrite $subscription $formattedSubscriptionIds -}}

{{- $rechargeError := dict 
        "http_status"  .response.statusCode
        "error_message" (or .rawData.errors .rawData.error)
-}}

{
    "error": {{- ($isSuccess | ternary nil $rechargeError) | toJson -}},
    "subscription": {{- ($isSuccess | ternary $subscription nil) | toJson -}} 
}
