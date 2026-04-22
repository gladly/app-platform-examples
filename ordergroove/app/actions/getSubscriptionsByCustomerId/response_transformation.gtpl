[
{{- range $index, $subscription := .rawData.results -}}
{{- if $index -}},{{- end }}
{
  "customer": {{if $subscription.customer}}"{{$subscription.customer}}"{{else}}null{{end}},
  "merchant": {{if $subscription.merchant}}"{{$subscription.merchant}}"{{else}}null{{end}},
  "product": {{if $subscription.product}}"{{$subscription.product}}"{{else}}null{{end}},
  "payment": {{if $subscription.payment}}"{{$subscription.payment}}"{{else}}null{{end}},
  "shipping_address": {{if $subscription.shipping_address}}"{{$subscription.shipping_address}}"{{else}}null{{end}},
  "offer": {{if $subscription.offer}}"{{$subscription.offer}}"{{else}}null{{end}},
  "subscription_type": {{if $subscription.subscription_type}}"{{$subscription.subscription_type}}"{{else}}null{{end}},
  "components": {{if $subscription.components}}{{toJson $subscription.components}}{{else}}null{{end}},
  "extra_data": {{toJson $subscription.extra_data}},
  "public_id": {{if $subscription.public_id}}"{{$subscription.public_id}}"{{else}}null{{end}},
  "product_attribute": {{if $subscription.product_attribute}}"{{$subscription.product_attribute}}"{{else}}null{{end}},
  "quantity": {{if $subscription.quantity}}{{$subscription.quantity}}{{else}}null{{end}},
  "price": {{if $subscription.price}}"{{$subscription.price}}"{{else}}null{{end}},
  "frequency_days": {{if $subscription.frequency_days}}{{$subscription.frequency_days}}{{else}}null{{end}},
  "reminder_days": {{if $subscription.reminder_days}}{{$subscription.reminder_days}}{{else}}null{{end}},
  "every": {{if $subscription.every}}{{$subscription.every}}{{else}}null{{end}},
  "every_period": {{if $subscription.every_period}}{{$subscription.every_period}}{{else}}null{{end}},
  "start_date": {{if $subscription.start_date}}"{{date "2006-01-02T15:04:05Z" (toDate "2006-01-02" $subscription.start_date)}}"{{else}}null{{end}},
  "cancelled": {{if and $subscription.cancelled (ne $subscription.cancelled "null")}}"{{date "2006-01-02T15:04:05Z" (toDate "2006-01-02" $subscription.cancelled)}}"{{else}}null{{end}},
  "cancel_reason": {{if $subscription.cancel_reason}}"{{$subscription.cancel_reason}}"{{else}}null{{end}},
  "cancel_reason_code": {{if $subscription.cancel_reason_code}}"{{$subscription.cancel_reason_code}}"{{else}}null{{end}},
  "currency_code": {{if $subscription.currency_code}}"{{$subscription.currency_code}}"{{else}}null{{end}},
  "iteration": {{if $subscription.iteration}}"{{$subscription.iteration}}"{{else}}null{{end}},
  "sequence": {{if $subscription.sequence}}"{{$subscription.sequence}}"{{else}}null{{end}},
  "session_id": {{if $subscription.session_id}}"{{$subscription.session_id}}"{{else}}null{{end}},
  "merchant_order_id": {{if $subscription.merchant_order_id}}"{{$subscription.merchant_order_id}}"{{else}}null{{end}},
  "customer_rep": {{if $subscription.customer_rep}}"{{$subscription.customer_rep}}"{{else}}null{{end}},
  "club": {{if $subscription.club}}"{{$subscription.club}}"{{else}}null{{end}},
  "created": {{if $subscription.created}}"{{date "2006-01-02T15:04:05Z" (toDate "2006-01-02 15:04:05" $subscription.created)}}"{{else}}null{{end}},
  "updated": {{if $subscription.updated}}"{{date "2006-01-02T15:04:05Z" (toDate "2006-01-02 15:04:05" $subscription.updated)}}"{{else}}null{{end}},
  "live": {{if ne $subscription.live nil}}{{$subscription.live}}{{else}}null{{end}},
  "prepaid_subscription_context": {{if $subscription.prepaid_subscription_context}}{{toJson $subscription.prepaid_subscription_context}}{{else}}null{{end}}
}
{{- end }}
]
