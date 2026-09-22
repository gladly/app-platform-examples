{{- /* Apply or remove a discount on a charge -- only the URL verb differs (mirrors
       manage_next_charge_skip's skip/unskip split). */ -}}
{{- $chargeId := .inputs.chargeId -}}
{{- $isApply := eq (.inputs.operation | toString) "apply" -}}

{{- if $isApply -}}
{{- printf "https://api.rechargeapps.com/charges/%s/apply_discount" $chargeId -}}
{{- else -}}
{{- printf "https://api.rechargeapps.com/charges/%s/remove_discount" $chargeId -}}
{{- end -}}
