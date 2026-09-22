{{- /* apply sends the discount code; remove needs no body. */ -}}
{{- $isApply := eq (.inputs.operation | toString) "apply" -}}
{{- if $isApply -}}
{{ toJson (dict "discount_code" .inputs.discountCode) }}
{{- else -}}
{}
{{- end -}}
