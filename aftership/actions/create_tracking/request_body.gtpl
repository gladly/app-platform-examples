{
    "tracking_number": "{{.inputs.trackingNumber}}"
    {{- if .inputs.orderID -}}
    ,
    "order_id": "{{.inputs.orderID}}"
    {{- end -}}
    {{- if .inputs.orderNumber -}}
    ,
    "order_number": "{{.inputs.orderNumber}}"
    {{- end }}
}