{{- $subscriptionId := .inputs.subscriptionId -}} 

{{- printf "https://api.rechargeapps.com/subscriptions/%s/cancel" $subscriptionId -}}