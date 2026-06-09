{{- if eq .inputs.reason nil }}
	{{- stop "Input reason is required. Cancellations always carry a cancellation reason." -}}
{{- end }}
{{- $reason := .inputs.reason | toString | trim -}}
{{- if eq $reason "" }}
	{{- stop "Input reason is required. Cancellations always carry a cancellation reason." -}}
{{- end }}

{{- /* when the merchant configured an allowed-reasons list, the reason must come from it */ -}}
{{- $configured := "" -}}
{{- if .integration.configuration.cancellationReasons -}}
    {{- $configured = .integration.configuration.cancellationReasons | toString | trim -}}
{{- end -}}
{{- if ne $configured "" -}}
    {{- $ok := false -}}
    {{- range splitList "," $configured -}}
        {{- if eq (. | trim) $reason -}}{{- $ok = true -}}{{- end -}}
    {{- end -}}
    {{- if not $ok }}
	{{- stop (printf "Cancellation reason %q is not in the configured list of allowed reasons." $reason) -}}
    {{- end }}
{{- end -}}
{ "cancellationReasons": [{{ $reason | toJson }}], "initiator": "MERCHANT" }
