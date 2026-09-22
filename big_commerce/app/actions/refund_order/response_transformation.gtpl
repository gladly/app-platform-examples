{{- $isSuccess := eq .response.statusCode 200 -}}
{{- $responseData := fromJson (.response.body | toString) -}}

{
    "success": {{- $isSuccess -}}, 
    "input_order_id": "{{- .inputs.orderId | int64 }}",
    "error_message": {{- ($isSuccess | ternary nil $responseData.title) | toJson -}}, 
    "http_status": {{- .response.statusCode -}},
    {{- if $isSuccess }}
    "refund": {
        "items": [
            {{- range $index, $item := $responseData.data.items }}
                {
                    "item_type": "{{ $item.item_type }}",
                    "item_id": "{{ $item.item_id | int64 }}",
                    "requested_amount": {{- toJson $item.requested_amount -}},
                    "quantity": {{- toJson $item.quantity -}},
                    "reason": {{- toJson $item.reason -}}
                }
            {{- if lt (add $index 1) (len $responseData.data.items) -}},{{- end -}} 
            {{- end }}
        ],
        "order_id": "{{  $responseData.data.order_id | int64}}",
        "reason": {{- toJson $responseData.data.reason -}},
        "total_amount": {{- toJson $responseData.data.total_amount -}},
        "total_tax": {{- toJson $responseData.data.total_tax -}},
        "uses_merchant_override_values": {{- toJson $responseData.data.uses_merchant_override_values -}}
    }
    {{- else }}
    "refund": null
    {{- end }}
}
