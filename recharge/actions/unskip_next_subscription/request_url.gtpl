{{- $chargeId := .inputs.chargeId -}} 

{{- printf "https://api.rechargeapps.com/charges/%s/unskip" $chargeId -}}