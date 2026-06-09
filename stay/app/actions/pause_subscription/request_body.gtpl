{{- if or (eq .inputs.pausedUntil nil) (eq (.inputs.pausedUntil | toString) "") }}
	{{- stop "Input pausedUntil is required. Pauses always carry a resume date." -}}
{{- else }}
{ "pausedUntil": {{ .inputs.pausedUntil | toJson }} }
{{- end }}
