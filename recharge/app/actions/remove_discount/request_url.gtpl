{{- $chargeId := .inputs.chargeId -}}

{{- printf "https://api.rechargeapps.com/charges/%s/remove_discount" $chargeId -}}
