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

{{- $charge := .rawData.charge -}}

{{- /* create a map with formatted charge IDs as strings to ensure proper graphql output formatting */ -}}
{{- $formattedChargeIds := dict
    "id" ($charge.id | int64 | toString)
    "address_id" ($charge.address_id | int64 | toString)
    "customer" (dict
        "id" ($charge.customer.id | int64 | toString))
-}}

{{- /* convert all line items purchase_item_ids to strings to ensure proper graphql output formatting */ -}}
{{- range $index, $line_item := $charge.line_items -}}
    {{- $_ := set . "purchase_item_id" ($line_item.purchase_item_id | int64 | toString) -}}
{{- end -}}

{{- /* merge the formatted IDs back into the charge data */}}
{{- $charge = mergeOverwrite $charge $formattedChargeIds -}}

{
    "error": {{- ($isSuccess | ternary nil $rechargeError) | toJson -}},
    "charge": {{- ($isSuccess | ternary $charge nil) | toJson -}} 
}
