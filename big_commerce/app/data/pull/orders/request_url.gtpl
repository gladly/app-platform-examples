{{/* Fetch all orders for a customer using Big Commerce customer id. */}}
https://api.bigcommerce.com/stores/{{.integration.configuration.store}}/v2/orders?customer_id={{(index .externalData.big_commerce_customer 0).id}}&sort=date_created:desc
