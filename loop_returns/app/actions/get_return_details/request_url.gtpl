{{- if not (or .inputs.returnId .inputs.orderId .inputs.orderName) -}}
{{- "a return lookup requires at least one of: returnId, orderId, or orderName" | stop -}}
{{- end -}}
https://api.loopreturns.com/api/v1/warehouse/return/details?
{{- $params := list -}}
{{- if .inputs.returnId }}{{ $params = append $params (printf "return_id=%v" .inputs.returnId) }}{{ end -}}
{{- if .inputs.orderId }}{{ $params = append $params (printf "order_id=%v" .inputs.orderId) }}{{ end -}}
{{- if .inputs.orderName }}{{ $params = append $params (printf "order_name=%s" (.inputs.orderName | urlquery)) }}{{ end -}}
{{- if .inputs.currencyType }}{{ $params = append $params (printf "currency_type=%s" .inputs.currencyType) }}{{ end -}}
{{- join "&" $params -}}
