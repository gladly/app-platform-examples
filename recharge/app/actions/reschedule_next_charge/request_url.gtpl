{{- $subscriptionId := .inputs.subscriptionId -}}

{{- printf "https://api.rechargeapps.com/subscriptions/%s/set_next_charge_date" $subscriptionId -}}
