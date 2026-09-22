{{- $chargeId := .inputs.chargeId -}}

{{- printf "https://api.rechargeapps.com/charges/%s/apply_discount" $chargeId -}}
