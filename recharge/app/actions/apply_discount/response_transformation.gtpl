{{- $isSuccess := and (ge (int .response.statusCode) 200) (lt (int .response.statusCode) 300) -}}

{{- /* KTD1: the runtime handles auth errors (401/403); fail loudly on unexpected >=500. */ -}}
{{- if and (not $isSuccess) (ge (int .response.statusCode) 500) -}}
    {{- fail (printf "Recharge returned an unexpected status code: %d" (int .response.statusCode)) -}}
{{- end -}}

{{- /* Collapse Recharge's 4xx error shapes into one message: a bare string,
       a map of field -> "message", or a map of field -> ["message", ...]. */ -}}
{{- $errorMessages := "" -}}
{{- if not $isSuccess -}}
    {{- $errors := .rawData.errors -}}
    {{- if eq (typeOf $errors) "string" -}}
        {{- $errorMessages = $errors -}}
    {{- else if kindIs "map" $errors -}}
        {{- $parts := list -}}
        {{- range $key, $value := $errors -}}
            {{- if eq (typeOf $value) "string" -}}
                {{- $parts = append $parts $value -}}
            {{- else if eq (typeOf $value) "[]interface {}" -}}
                {{- range $message := $value -}}
                    {{- $parts = append $parts (toString $message) -}}
                {{- end -}}
            {{- end -}}
        {{- end -}}
        {{- $errorMessages = join "; " (uniq $parts) -}}
    {{- end -}}
{{- end -}}

{{- /* Never hand the agent a blank failure: if no known error shape matched (an unknown
       body, an absent errors key, or a 3xx), name the status code instead. */ -}}
{{- if and (not $isSuccess) (eq $errorMessages "") -}}
    {{- $errorMessages = printf "Recharge returned an error (HTTP %d)." (int .response.statusCode) -}}
{{- end -}}

{{- $rechargeError := dict
        "http_status"  .response.statusCode
        "error_message" $errorMessages
-}}

{{- $charge := .rawData.charge -}}

{{- if $isSuccess -}}
    {{- /* Stringify int64 IDs. Free-gift line items have a null purchase_item_id; leave those null
           (the LineItem.purchase_item_id field is nullable) and only stringify real ones. */ -}}
    {{- $formattedChargeIds := dict
            "id" ($charge.id | int64 | toString)
            "address_id" ($charge.address_id | int64 | toString)
    -}}
    {{- /* Guard the nested customer: a charge can come back without it; an unguarded
           .customer.id deref would EXEC-error and fail the action after the gift already landed. */ -}}
    {{- if $charge.customer -}}
        {{- $_ := set $formattedChargeIds "customer" (dict "id" ($charge.customer.id | int64 | toString)) -}}
    {{- end -}}
    {{- range $index, $line_item := $charge.line_items -}}
        {{- if $line_item.purchase_item_id -}}
            {{- $_ := set $line_item "purchase_item_id" ($line_item.purchase_item_id | int64 | toString) -}}
        {{- end -}}
    {{- end -}}
    {{- $charge = mergeOverwrite $charge $formattedChargeIds -}}
{{- end -}}

{
    "error": {{- ($isSuccess | ternary nil $rechargeError) | toJson -}},
    "charge": {{- ($isSuccess | ternary $charge nil) | toJson -}}
}
