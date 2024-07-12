[
{{- $firstTransaction :=  true}}
{{- range .rawData.results -}}
    {{- $externalCustomerID := .externalCustomerId }}
    {{- range $i, $transaction := .transactions}}
        {{- if ne $transaction.type "ORDER" }}
            {{- continue}}
        {{- end}}
    	{{ with $transaction -}}{{- if not $firstTransaction}},{{end}}
		{
			"externalCustomerId": {{toJson $externalCustomerID}},
			"id": {{toJson .orderNumber}},
			"createdAt": {{template "optionalTimestamp" .createdAt}},
			"status": {{template "optionalString" .orderStatus}},
			"adminOrderUrl": {{template "optionalString" .orderLink}},
			"totalAmount": {{if and .orderTotal (kindIs "string" .orderTotal)}}{{with $totalAmount := regexFind "[0-9]+(?:\\.[0-9]+)?|\\.[0-9]+" .orderTotal}}{{if eq (substr 0 1 $totalAmount) "."}}0{{$totalAmount}}{{else}}{{$totalAmount}}{{end}}{{else}}null{{end}}{{else}}null{{end}},
			"currencyCode": "{{if .currencyCode}}{{.currencyCode}}{{else}}USD{{end}}",
			"itemCount": {{template "optionalNumber" .itemCount}},
			"customerOrderUrl": {{template "optionalString" .customerOrderUrl}},
			"note": {{template "optionalString" .note}},
			"products": [
				{{range $j, $product := .products -}}{{- if gt $j 0 }},{{end}}
				{
					"id": {{template "optionalString" $product.id}},
					"name": {{template "optionalString" $product.name}},
					"status": {{template "optionalString" $product.status}},
					"sku": {{template "optionalString" $product.sku}},
					"imageUrl": {{template "optionalString" $product.imageUrl}},
					"quantity": {{template "optionalNumber" $product.quantity}}
				}
				{{end -}}
			],
			"fulfillments": [
				{{range $j, $fulfillment := .fulfillments -}}{{- if gt $j 0 }},{{end}}
				{
					"estimatedDeliveryDate": {{template "optionalTimestamp" $fulfillment.estimatedDeliveryDate}},
					"trackingUrl": {{template "optionalString" $fulfillment.trackingUrl}},
					"trackingNumber": {{template "optionalString" $fulfillment.trackingNumber}},
					"productIds": {{if $fulfillment.productIds}}{{toJson $fulfillment.productIds}}{{else}}[]{{end}},
					"status": {{template "optionalString" $fulfillment.status}},
					"carrier": {{template "optionalString" $fulfillment.carrier}},
					"shippingSpeed": {{template "optionalString" $fulfillment.shippingSpeed}},
					"shippingAddress": {{template "optionalAddress" $fulfillment.shippingAddress}}
				}
				{{end -}}
			],
			"billingAddress": {{template "optionalAddress" .billingAddress}}
		}
        {{- end}}
		{{- $firstTransaction = false}}
    {{- end}}
{{ end -}}
]

{{- define "optionalString"}}{{if and . (kindIs "string" .)}}{{toJson .}}{{else}}""{{end}}{{end -}}

{{- define "optionalNumber"}}{{if or (eq . nil) (not (kindIs "float64" .))}}null{{else}}{{.}}{{end}}{{end -}}

{{- define "optionalTimestamp"}}
	{{- if not . -}}
		null
	{{- else}}
		{{- $timeStamp := toDate "2006-01-02T15:04:05Z07:00" .}}
		{{- if not $timeStamp.IsZero -}}
			"{{.}}"
		{{- else -}}
			null
		{{- end}}
	{{- end}}
{{- end}}

{{- define "optionalAddress"}}
	{{- if . -}}
	{
		"name": {{template "optionalString" .name}},
		"phone": {{template "optionalString" .phone}},
		"street1": {{template "optionalString" .street1}},
		"street2": {{template "optionalString" .street2}},
		"city": {{template "optionalString" .city}},
		"state": {{template "optionalString" .state}},
		"country": {{template "optionalString" .country}},
		"zip": {{template "optionalString" .zip}}
	}
	{{- else -}}
	null
	{{- end}}
{{- end}}
