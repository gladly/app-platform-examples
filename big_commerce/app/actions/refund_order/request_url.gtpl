{{- $orderId := .inputs.orderId | int64 | toString -}} 
{{- $storeHash := .integration.configuration.store -}}

{{- printf "https://api.bigcommerce.com/stores/%s/v3/orders/%s/payment_actions/refunds" $storeHash $orderId -}} 
 
