{{- $isSuccess := eq .response.statusCode 200 -}}
{{- $responseData := fromJson (.response.body | toString) -}}

{
    "input_order_id": "{{- .inputs.orderId | int64 -}}",
    "success": {{- $isSuccess -}},
    "http_status": {{- .response.statusCode -}}, 
    "error_message": {{- ($isSuccess | ternary nil $responseData.title) | toJson -}},
    "quote": {{- ($isSuccess | ternary $responseData.data nil) | toJson -}}
}

