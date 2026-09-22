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

    {{- /* camelCase aliases for the profile card: the flexible.card XSD forbids underscores in
           dataSource refs and the platform does not camelCase snake_case fields at the card layer.
           Declared on the Customer data type; mirror the snake_case fields 1:1. */ -}}
    {{- $cardAliases := dict
        "subscriptionsActiveCount" $customer.subscriptions_active_count
        "subscriptionsTotalCount" $customer.subscriptions_total_count
        "hasPaymentMethodInDunning" $customer.has_payment_method_in_dunning
        "hasValidPaymentMethod" $customer.has_valid_payment_method
    -}}

    {{- /* merge the formatted ID field and card aliases back into the customer data */ -}}
    {{- $customer = mergeOverwrite $customer $formattedCustomerFields $cardAliases -}}

    {{- $customer | toJson -}}
{{- end}}
