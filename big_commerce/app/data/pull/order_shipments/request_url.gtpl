{{- $store_hash := .integration.configuration.store}}
{{/* Iterate over all orders to create the URL for fetching the associated shipments. */}}
{{- range $dataIndex, $order := .externalData.big_commerce_order}}
https://api.bigcommerce.com/stores/{{$store_hash}}/v2/orders/{{$order.id}}/shipments
{{- end}}
