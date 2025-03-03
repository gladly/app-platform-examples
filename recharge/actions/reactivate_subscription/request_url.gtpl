{{- $subscriptionId := .inputs.subscriptionId -}} 

{{- printf "https://api.rechargeapps.com/subscriptions/%s/activate" $subscriptionId -}}