{{- $isSuccess := eq .response.statusCode 200 -}}

{{- /* KTD1: the runtime handles auth errors (401/403); fail loudly on unexpected >=500. */ -}}
{{- if and (not $isSuccess) (ge (int .response.statusCode) 500) -}}
    {{- fail (printf "Recharge returned an unexpected status code: %d" (int .response.statusCode)) -}}
{{- end -}}

{{- /* Recharge returns `errors` as a string (e.g. 404) or an object of field->[messages]
       (e.g. 422 validation). Flatten to a single string so it conforms to the String
       error_message field. Both /cancel and /activate return this same shape. */ -}}
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
        "error_message" $errorMessages
-}}

{
    "error": {{- ($isSuccess | ternary nil $rechargeError) | toJson -}},
    "subscription": {{- ($isSuccess | ternary $subscription nil) | toJson -}}
}
