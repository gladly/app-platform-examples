{{/* Fetch all orders for a customer using Big Commerce customer id. */}}
{{ $ol := .integration.configuration.ordersLimit}}
{{- $ordersLimit := $ol | default 10 -}}

https://api.bigcommerce.com/stores/{{.integration.configuration.store}}/v2/orders?customer_id={{(index .externalData.big_commerce_customer 0).id}}&sort=date_created:desc&limit={{$ordersLimit}}
