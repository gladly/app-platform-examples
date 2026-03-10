{{- if not .rawData.items -}}
[]
{{- else -}}
{{- $results := list -}}
{{- range .rawData.items -}}
    {{- $shipment := dict
        "shipmentId" (toString (int64 .entity_id))
        "orderId" (toString (int64 .order_id))
    -}}
    {{- if .tracks -}}
        {{- $track := first .tracks -}}
        {{- $_ := set $shipment "trackingNumber" $track.track_number -}}
        {{- $_ := set $shipment "shipmentMethod" $track.title -}}
        {{- if $track.carrier_code -}}
            {{- $_ := set $shipment "carrier" $track.carrier_code -}}
        {{- end -}}
        {{- if $track.tracking_url -}}
            {{- $_ := set $shipment "trackingUrl" $track.tracking_url -}}
        {{- end -}}
    {{- end -}}
    {{- $shipmentItems := list -}}
    {{- range .items -}}
        {{- $shipmentItems = append $shipmentItems (dict "productId" (toString (int64 .product_id))) -}}
    {{- end -}}
    {{- $_ := set $shipment "items" $shipmentItems -}}
    {{- $results = append $results $shipment -}}
{{- end -}}
{{- toJson $results -}}
{{- end -}}