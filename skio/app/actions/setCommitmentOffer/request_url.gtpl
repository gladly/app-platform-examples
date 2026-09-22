{{- /* Guards run here, not in the form - see "Merchant gates" in the app README. */ -}}

{{- /* Merchant opt-in, fail closed when unset. */ -}}
{{- $enabled := .integration.configuration.enableCommitmentOffer | default "" | toString | trim | lower -}}
{{- if not (or (eq $enabled "true") (eq $enabled "yes")) -}}
    {{- stop "Setting a commitment offer is not enabled for this app. An admin must set \"Allow agents to set commitment offers\" to true in the Skio app configuration first." -}}
{{- end -}}

{{- /* Confirmation checkbox: human-path friction, NOT the access control. */ -}}
{{- if ne (.inputs.confirmed | default false) true -}}
    {{- stop "Check the confirmation box to add required cycles to this subscription." -}}
{{- end -}}

{{- $cycles := .inputs.additionalCycles | int -}}
{{- if le $cycles 0 -}}
    {{- stop "Additional cycles must be a positive whole number." -}}
{{- end -}}
https://graphql.skio.com/v1/graphql
