{{- if and .response (ne .response.statusCode 200) -}}
  {{- if eq .response.statusCode 404 -}}
    {{ stop (printf "subscription with id %s does not exist" .inputs.subscriptionId) }}
  {{- end -}}
  {{ fail (printf "retrieve failed: %d %s" .response.statusCode .response.body) }}
{{- end -}}
{
  "customer": {{if .rawData.customer}}"{{.rawData.customer}}"{{else}}null{{end}},
  "merchant": {{if .rawData.merchant}}"{{.rawData.merchant}}"{{else}}null{{end}},
  "product": {{if .rawData.product}}"{{.rawData.product}}"{{else}}null{{end}},
  "payment": {{if .rawData.payment}}"{{.rawData.payment}}"{{else}}null{{end}},
  "shipping_address": {{if .rawData.shipping_address}}"{{.rawData.shipping_address}}"{{else}}null{{end}},
  "offer": {{if .rawData.offer}}"{{.rawData.offer}}"{{else}}null{{end}},
  "subscription_type": {{if .rawData.subscription_type}}"{{.rawData.subscription_type}}"{{else}}null{{end}},
  "components": {{if .rawData.components}}{{toJson .rawData.components}}{{else}}null{{end}},
  "extra_data": {{toJson .rawData.extra_data}},
  "public_id": {{if .rawData.public_id}}"{{.rawData.public_id}}"{{else}}null{{end}},
  "product_attribute": {{if .rawData.product_attribute}}"{{.rawData.product_attribute}}"{{else}}null{{end}},
  "quantity": {{if .rawData.quantity}}{{.rawData.quantity}}{{else}}null{{end}},
  "price": {{if .rawData.price}}"{{.rawData.price}}"{{else}}null{{end}},
  "frequency_days": {{if .rawData.frequency_days}}{{.rawData.frequency_days}}{{else}}null{{end}},
  "reminder_days": {{if .rawData.reminder_days}}{{.rawData.reminder_days}}{{else}}null{{end}},
  "every": {{if .rawData.every}}{{.rawData.every}}{{else}}null{{end}},
  "every_period": {{if .rawData.every_period}}{{.rawData.every_period}}{{else}}null{{end}},
  "start_date": {{if .rawData.start_date}}"{{date "2006-01-02T15:04:05Z" (toDate "2006-01-02" .rawData.start_date)}}"{{else}}null{{end}},
  "cancelled": {{if and .rawData.cancelled (ne .rawData.cancelled "null")}}"{{date "2006-01-02T15:04:05Z" (toDate "2006-01-02" .rawData.cancelled)}}"{{else}}null{{end}},
  "cancel_reason": {{if .rawData.cancel_reason}}"{{.rawData.cancel_reason}}"{{else}}null{{end}},
  "cancel_reason_code": {{if .rawData.cancel_reason_code}}"{{.rawData.cancel_reason_code}}"{{else}}null{{end}},
  "currency_code": {{if .rawData.currency_code}}"{{.rawData.currency_code}}"{{else}}null{{end}},
  "iteration": {{if .rawData.iteration}}"{{.rawData.iteration}}"{{else}}null{{end}},
  "sequence": {{if .rawData.sequence}}"{{.rawData.sequence}}"{{else}}null{{end}},
  "session_id": {{if .rawData.session_id}}"{{.rawData.session_id}}"{{else}}null{{end}},
  "merchant_order_id": {{if .rawData.merchant_order_id}}"{{.rawData.merchant_order_id}}"{{else}}null{{end}},
  "customer_rep": {{if .rawData.customer_rep}}"{{.rawData.customer_rep}}"{{else}}null{{end}},
  "club": {{if .rawData.club}}"{{.rawData.club}}"{{else}}null{{end}},
  "created": {{if .rawData.created}}"{{date "2006-01-02T15:04:05Z" (toDate "2006-01-02 15:04:05" .rawData.created)}}"{{else}}null{{end}},
  "updated": {{if .rawData.updated}}"{{date "2006-01-02T15:04:05Z" (toDate "2006-01-02 15:04:05" .rawData.updated)}}"{{else}}null{{end}},
  "live": {{if ne .rawData.live nil}}{{.rawData.live}}{{else}}null{{end}},
  "prepaid_subscription_context": {{if .rawData.prepaid_subscription_context}}{{toJson .rawData.prepaid_subscription_context}}{{else}}null{{end}}
}


