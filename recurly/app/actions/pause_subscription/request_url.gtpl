{{- if eq .inputs.subscriptionId nil }}
	{{- stop "Input subscriptionId is required." -}}
{{- else }}

{{- /* Cancel subscription by id. */ -}}
{{- /* inputs: subscriptionId */ -}}
https://v3.recurly.com/subscriptions/{{.inputs.subscriptionId}}/pause

{{- end }}
