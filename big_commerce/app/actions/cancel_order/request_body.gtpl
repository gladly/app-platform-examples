{{/*  
    statuses: https://developer.bigcommerce.com/docs/rest-management/orders/order-status#get-all-order-statuses
    update an order: https://developer.bigcommerce.com/docs/rest-management/orders#update-an-order 
*/}}

{{- $cancelledStatusId := 5 -}} 
{{- $hasReason := (ne .inputs.reason nil) -}}

{
    "status_id": {{- $cancelledStatusId -}},
    "staff_notes": {{- ($hasReason | ternary .inputs.reason "") | toJson -}}
}
