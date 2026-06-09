{{- if or (eq .inputs.subscriptionId nil) (eq (.inputs.subscriptionId | toString) "") }}
	{{- stop "Input subscriptionId is required." -}}
{{- else }}
{{- printf `https://api.retextion.com/api/v2/subscriptions/%s/pause` (.inputs.subscriptionId | toString | urlquery) -}}
{{- end }}
