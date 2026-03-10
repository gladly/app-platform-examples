{{- range .externalData.order -}}
https://{{$.integration.configuration.host}}/rest/V1/shipments?searchCriteria[filter_groups][0][filters][0][field]=order_id&searchCriteria[filter_groups][0][filters][0][value]={{.orderId}}&searchCriteria[filter_groups][0][filters][0][condition_type]=eq
{{ end -}}