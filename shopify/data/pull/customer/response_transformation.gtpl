{{/* If no customer is found or more than one matching customer is found return error. */}}
{{- $errors := .rawData.errors -}}
{{- $hasErrors := or (and $errors (gt (len $errors) 0)) false -}}

{{/* 
    Check if no customer is found
    - This check verifies if the response data contains no customer records.
*/}}

{{ if (ne .response.statusCode 200) }}
    {{- $error := toJson .rawData.errors -}}
	{{- fail  $error -}}
{{- else if .rawData.errors -}}
    {{/* make single string from errors. format: <field> - <message>; ... */}}
    {{- $errorMessages := "" -}}
    {{- range $index, $error := $errors -}}
        {{- $field := $error.message -}}
        {{- if $index -}}
            {{- $errorMessages = (print $errorMessages "; " $field " - " $error.extensions.code) -}}
        {{- else -}}
            {{- $errorMessages = (print $field " - " $error.extensions.code) -}}
        {{- end -}}
    {{- end -}}

    {{- fail $errorMessages -}}
{{-  else if and .rawData.data .rawData.data.customers (eq (len .rawData.data.customers.edges) 0) -}}
    {{- stop "Customer does not exist" -}}
{{-  else if and .rawData.data .rawData.data.customers (gt (len .rawData.data.customers.edges) 1) -}}
    {{- stop "Shopify returned more than one customer for the customer profile email or phone." -}}    
{{- else -}}
{
    {{/* Iterate through all customers. Simplify edges and nodes out of the response. */}}
    {{- $customer := (index .rawData.data.customers.edges 0).node}}
    "id": "{{ $customer.id }}",
    "email": "{{ $customer.email }}",
    "phone": {{ toJson $customer.phone }},
    "number_of_orders": "{{ $customer.numberOfOrders }}",
    "first_name": "{{ $customer.firstName }}",
    "last_name": "{{ $customer.lastName }}",
    "default_address": {{ toJson $customer.defaultAddress }},
    "total_spent": {{ toJson $customer.amountSpent }},
    "email_marketing_consent": {{ toJson $customer.emailMarketingConsent }},
    "sms_marketing_consent": {{ toJson $customer.smsMarketingConsent }},
    "lifetime_duration": "{{ $customer.lifetimeDuration }}",
    "note": {{ toJson $customer.note }},
    "tags": {{ toJson $customer.tags }},
    "created_at": {{ toJson $customer.createdAt }},
    "updated_at": {{ toJson $customer.updatedAt }}
}
{{- end -}}

