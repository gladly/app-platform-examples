{{- $chargeId := .inputs.chargeId -}} 

{{- printf "https://api.rechargeapps.com/charges/%s/skip" $chargeId -}}