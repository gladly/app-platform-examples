{{- range .externalData.sample_oms_order}}

{{- range .lineItems -}}
https://gladly-sample-oms.vercel.app/api/products/{{.productId}}
{{end}}

{{- end}}