{{- $subscriptionId := .inputs.subscriptionId -}}
{{- $isCancel := eq (.inputs.operation | toString) "cancel" -}}

{{- if $isCancel -}}
{{- printf "https://api.rechargeapps.com/subscriptions/%s/cancel" $subscriptionId -}}
{{- else -}}
{{- printf "https://api.rechargeapps.com/subscriptions/%s/activate" $subscriptionId -}}
{{- end -}}