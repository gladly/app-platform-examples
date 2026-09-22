{{- /* Check if the response is empty (204 No Content), which means no customer exists for the given email. */}}
{{- if (eq .response.statusCode 204) }}
    {{stop "No BigCommerce customer found for the customer profile email address."}}

{{- /* Check if .rawData or .rawData.data is missing. */}}
{{- else if or (eq .rawData nil) (eq .rawData.data nil) }}
    {{stop "No BigCommerce customer found for the customer profile email address."}}

{{- /* Check if no customer records are found in the response. */}}
{{- else if eq (len .rawData.data) 0 }}
    {{stop "No BigCommerce customer found for the customer profile email address."}}

{{- else }}
    {{- /* Check if more than one customer is returned (unexpected behavior). */}}
    {{- if gt (len .rawData.data) 1}}
        {{stop "BigCommerce returned more than one customer for the customer profile email address."}}
    {{- end}}

    {{- $customer := index .rawData.data 0 -}}

    {{- /* Handle conversion of ids to string correctly */}}
    {{- $_ := set $customer "id" ($customer.id | int64 | toString) -}}
    {{- $_ := set $customer "customer_group_id" ($customer.customer_group_id | int64 | toString) -}}
    
    {{- toJson $customer -}}
{{- end}}
