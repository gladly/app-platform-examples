{{- $isSuccess := eq .response.statusCode 200 -}}
{{- $responseData := fromJson (.response.body | toString) -}}

{{- $errorMessages := "" -}}

{{- if not $isSuccess -}}
    {{- if (eq (typeOf $responseData) "[]interface {}") -}}  {{/* Check if the response is an array */}}
        {{- range $index, $error := $responseData -}} 
            {{- if $index -}}
                {{- $errorMessages = print $errorMessages "; " $error.message " - " $error.status -}}
            {{- else -}}
                {{- $errorMessages = print $error.message " - " $error.status -}}
            {{- end -}}
        {{- end -}}
    {{- else -}}  {{/* Response is an object */}}
        {{- $errorMessages = print $responseData.title " - " $responseData.status -}}
    {{- end -}}
{{- end -}}

{
    "success": {{- $isSuccess -}},
    "input_order_id": "{{- .inputs.orderId | int64 -}}",
    "http_status": {{- .response.statusCode -}}, 
    "error_message": {{- ($isSuccess | ternary nil $errorMessages) | toJson -}}
}
