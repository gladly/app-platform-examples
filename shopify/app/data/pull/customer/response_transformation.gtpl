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
{{-  else if and .rawData.data .rawData.data.customers (eq (len .rawData.data.customers.nodes) 0) -}}
    {{- stop "Customer does not exist" -}}
{{-  else if and .rawData.data .rawData.data.customers (gt (len .rawData.data.customers.nodes) 1) -}}
    {{- stop "Shopify returned more than one customer for the customer profile email or phone." -}}    
{{- else -}}

{{/* Iterate through all customers. Simplify nodes and nodes out of the response. */}}
{{- $customer := (index .rawData.data.customers.nodes 0) -}}

{{- /* Shopify deprecated Customer.email and Customer.phone. We read the
       non-deprecated defaultEmailAddress / defaultPhoneNumber instead and flatten
       them back onto the field names shopify_customer already publishes, so the
       data type is unchanged. Set explicitly in both branches: an absent key and
       a null one are not the same fixture. */ -}}
{{- if $customer.defaultEmailAddress -}}
    {{- $_ := set $customer "email" $customer.defaultEmailAddress.emailAddress -}}
{{- else -}}
    {{- $_ := set $customer "email" nil -}}
{{- end -}}
{{- $_ := unset $customer "defaultEmailAddress" -}}

{{- if $customer.defaultPhoneNumber -}}
    {{- $_ := set $customer "phone" $customer.defaultPhoneNumber.phoneNumber -}}
{{- else -}}
    {{- $_ := set $customer "phone" nil -}}
{{- end -}}
{{- $_ := unset $customer "defaultPhoneNumber" -}}

{{- /* The metafields data pull caps each owner's metafields, so a short list
       is indistinguishable from a complete one. Carry Shopify's hasNextPage
       for the customer's own metafields and drop the connection wrapper: the
       metafields themselves come from the metafields data pull. */ -}}
{{- if $customer.metafields -}}
    {{- $_ := set $customer "hasMoreMetafields" $customer.metafields.pageInfo.hasNextPage -}}
{{- else -}}
    {{- $_ := set $customer "hasMoreMetafields" nil -}}
{{- end -}}
{{- $_ := unset $customer "metafields" -}}

{{- with $customer -}}
{{ toJson . -}}
{{- end -}}

{{- end -}}

