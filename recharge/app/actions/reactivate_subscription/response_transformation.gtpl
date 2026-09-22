{{- $isSuccess := eq .response.statusCode 200 -}}

{{- $errorMessages := "" -}}

{{- if not $isSuccess -}}
    {{- $errors := .rawData.errors  -}}
    {{- if eq (typeOf $errors) "string" -}}
        {{- $errorMessages = $errors -}}
    {{- else -}}
        {{- range $key, $value := $errors -}}
            {{- if eq (typeOf $value) "[]interface {}" -}}
                {{- range $index, $message := $value -}}
                    {{- $separator := ((eq $index 0) | ternary "" "; ") -}}
                    {{- $errorMessages = print $errorMessages $separator $message -}}
                {{- end -}}
            {{- end -}}
        {{- end -}}
    {{- end -}}
{{- end -}}

{{- $rechargeError := dict 
        "http_status"  .response.statusCode
        "error_message" $errorMessages
-}}

{{- $subscription := .rawData.subscription -}}

{{- /* create a map with formatted subscription IDs as strings to ensure proper graphql output formatting */}}
{{- $formattedSubscriptionIds := dict 
        "id" ($subscription.id | int64 | toString) 
        "customer_id" ($subscription.customer_id | int64 | toString) 
        "address_id" ($subscription.address_id | int64 | toString) 
-}}

{{- /* merge the formatted IDs back into the subscription data */}}
{{- $subscription = mergeOverwrite $subscription $formattedSubscriptionIds -}}
{
    "error": {{- ($isSuccess | ternary nil $rechargeError) | toJson -}},
    "subscription": {{- ($isSuccess | ternary $subscription nil) | toJson -}} 
}
