{{- /* Check for API errors first */ -}}
{{- if and .response.statusCode (ne .response.statusCode 200) -}}
    {{- if .rawData.detail -}}
        {{- fail .rawData.detail -}}
    {{- else -}}
        {{- fail "API request failed" -}}
    {{- end -}}
{{- else if and .rawData .rawData.results -}}
[
{{- range $i, $item := .rawData.results -}}
{{- if $i -}},{{- end }}
{{- /* Transform date fields while preserving the rest of the item object */ -}}
{{- if $item.order_updated -}}
    {{- $_ := set $item "order_updated" ($item.order_updated | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}
{{- end -}}
{{- if and $item.first_placed (ne $item.first_placed "null") (ne $item.first_placed "") -}}
    {{- $_ := set $item "first_placed" ($item.first_placed | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}
{{- end -}}
{{- /* Convert empty strings to null */ -}}
{{- if eq $item.product_attribute "" -}}
    {{- $_ := set $item "product_attribute" nil -}}
{{- end -}}
{{- if and $item.components (eq (len $item.components) 0) -}}
    {{- $_ := set $item "components" nil -}}
{{- end -}}
{{- if eq $item.subscription_component "" -}}
    {{- $_ := set $item "subscription_component" nil -}}
{{- end -}}
{{- if eq $item.extra_cost "" -}}
    {{- $_ := set $item "extra_cost" nil -}}
{{- end -}}
{{- /* Card-safe camelCase twins. The flexible.card XSD forbids underscores in
       dataSource refs and the platform does not camelCase these at the card layer. */ -}}
{{- $_ := set $item "publicId" $item.public_id -}}
{{- $_ := set $item "totalPrice" $item.total_price -}}
{{- $_ := set $item "extraCost" $item.extra_cost -}}
{{- $_ := set $item "productAttribute" $item.product_attribute -}}
{{- $_ := set $item "oneTime" $item.one_time -}}
{{- $_ := set $item "orderUpdated" $item.order_updated -}}
{{- $_ := set $item "firstPlaced" $item.first_placed -}}
{{- $_ := set $item "subscriptionComponent" $item.subscription_component -}}
{{- /* Int renders as "2.00" through a NumericValue binding, so give the card a String. */ -}}
{{- if ne $item.quantity nil -}}
  {{- $_ := set $item "quantityLabel" (printf "%d" (int $item.quantity)) -}}
{{- end -}}
{{toJson $item}}
{{- end -}}
]
{{- else -}}
[]
{{- end -}}