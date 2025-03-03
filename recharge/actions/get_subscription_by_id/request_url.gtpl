{{- $subscriptionId := .inputs.subscriptionId -}} 

{{- printf "https://api.rechargeapps.com/subscriptions?ids=%s" $subscriptionId -}}