{{if .rawData.data.orders}}
[
  {{range $index, $order := .rawData.data.orders}}
  {{if lt $index 10}}
  {{if $index}},{{end}}
  {
    "id": "{{$order.name}}",
    "name": "{{$order.name}}",
    "shipmentUrl": "https://app.wonderment.com/shipments?order_name={{$order.name | urlquery}}",
    "shipments": [
      {{range $shipIndex, $shipment := $order.shipments}}{{if $shipIndex}},{{end}}
      {
        "trackingCode": {{toJson $shipment.trackingCode}},
        "status": {{toJson $shipment.status}},
        "carrierDisplayName": {{toJson $shipment.carrierDisplayName}}
      }
      {{end}}
    ]
  }
  {{end}}
  {{end}}
]
{{else}}
[]
{{end}}
