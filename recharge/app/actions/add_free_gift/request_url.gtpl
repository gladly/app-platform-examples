{{- $chargeId := .inputs.chargeId -}}

{{- printf "https://api.rechargeapps.com/charges/%s/add_free_gift" $chargeId -}}
