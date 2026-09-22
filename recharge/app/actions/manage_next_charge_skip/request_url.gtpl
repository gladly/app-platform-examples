{{- $chargeId := .inputs.chargeId -}}
{{- $isSkip := eq (.inputs.operation | toString) "skip" -}}

{{- if $isSkip -}}
{{- printf "https://api.rechargeapps.com/charges/%s/skip" $chargeId -}}
{{- else -}}
{{- printf "https://api.rechargeapps.com/charges/%s/unskip" $chargeId -}}
{{- end -}}