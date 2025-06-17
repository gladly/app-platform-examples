{{- if or (eq .rawData nil) (eq .rawData.customers nil) (eq (len .rawData.customers) 0) }}
    {{stop "No Recharge customer found for the customer profile email address."}}
{{- else if gt (len .rawData.customers) 1}}
    {{stop "Recharge returned more than one customer for the customer profile email address"}}
{{- else }}
    {{- $customer := first .rawData.customers -}}

    {{- /* create a map with formatted customer ID as string fields to ensure proper graphql output formatting */ -}}
    {{- $formattedCustomerFields := dict 
        "id" ($customer.id | int64 | toString) 
    -}}

    {{- /* merge the formatted ID field back into the customer data */ -}}
    {{- $customer = mergeOverwrite $customer $formattedCustomerFields -}}

    {{- $customer | toJson -}}
{{- end}}
