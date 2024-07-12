{{/*
    Check if the response is empty (204 No Content)
    - The purpose of this check is to handle the scenario where no customer is found for the given email address.
    - If the response status code is 204, it means no customer exists.
*/}}
{{- if (eq .response.statusCode 204) }}
     {{stop "No BigCommerce customer found for the customer profile email address."}}

{{/* 
    Check if .rawData or .rawData.data is nil
    - This check ensures that the response data is not nil before further processing.
*/}}
{{- else if or (eq .rawData nil) (eq .rawData.data nil) }}
    {{stop "No BigCommerce customer found for the customer profile email address."}}
{{/* 
    Check if no BigCommerce customer is found
    - This check verifies if the response data contains no customer records.
*/}}
{{- else if eq (len .rawData.data) 0 }}
    {{stop "No BigCommerce customer found for the customer profile email address."}}
{{- else }}
     {{/*
        Don't process the template if more than one customer is returned.
        - This prevents processing when multiple customer records are returned, as it's not expected behavior.
    */}}
    {{- if gt (len .rawData.data) 1}}
        {{stop "BigCommerce returned more than one customer for the customer profile email address."}}
    {{- end}}

     {{/*
        Iterate over all customers.
        - This loop processes each customer record individually.
    */}}
    {{- range .rawData.data -}}
        {{/*
            Remove key value pairs from the data because they are not needed.
            - This step cleans up unnecessary data before further processing.
        */}}
        {{- $_ := unset . "registration_ip_address" }}
        {{- $_ := unset . "tax_exempt_category" }}
        {{- $_ := unset . "accepts_product_review_abandoned_cart_emails" }}
        {{- $_ := unset . "address_count" }}
        {{- $_ := unset . "attribute_count" }}
        {{- $_ := unset . "authentication" }}
        {{- $_ := unset . "store_credit_amounts" }}
        {{- $_ := unset . "channel_ids" }}  
        {{- $_ := unset . "origin_channel_id" }}  
    {{- end}}
    {{- toJson .rawData.data -}}
{{- end}}
