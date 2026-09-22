{{/*
    Check if the response is empty (204 No Content) or if there's no raw data.
    - The purpose of this check is to handle the scenario where no order products are found for the given order_id.
    - If the response status code is 204 or there's no raw data, it means no order products exists.
*/}}
{{- if or (eq .response.statusCode 204) (eq .rawData nil) }}
    {{stop "No products found for BigCommerce order."}}
{{- else }}
    {{/*
        Iterate over all order products.
        - This loop processes each order product record individually.
    */}}
    {{- range .rawData -}}

        {{/* Handle conversion of ids to string correctly */}}
        {{- $_ := set . "id" (.id | int64 | toString) -}}
        {{- $_ := set . "order_id" (.order_id | int64 | toString) -}}
        {{- $_ := set . "order_address_id" (.order_address_id | int64 | toString) -}}
        {{- $_ := set . "product_id" (.product_id | int64 | toString) -}}
        
    {{- end}}
    {{- toJson .rawData -}}
{{- end}}
