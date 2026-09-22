{{- if eq .inputs.subscriptionId nil }}
	{{- stop "Input subscriptionId is required." -}}
{{- else }}

{{- /* Terminate (immediately expire) a subscription by id. */ -}}
{{- /* inputs: subscriptionId, refund (full|partial|none) */ -}}
https://v3.recurly.com/subscriptions/{{.inputs.subscriptionId}}{{if .inputs.refund}}?refund={{.inputs.refund}}{{end}}

{{- end }}
