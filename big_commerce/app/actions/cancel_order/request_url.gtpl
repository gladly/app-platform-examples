{{- $orderId := .inputs.orderId | int64 | toString -}} 
{{- $storeHash := .integration.configuration.store -}}

{{- printf "https://api.bigcommerce.com/stores/%s/v2/orders/%s" $storeHash $orderId -}} 
 
 