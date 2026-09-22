{{- /* Guards run here, not in the form - see "Merchant gates" in the app README. */ -}}

{{- /* Merchant opt-in, fail closed when unset. */ -}}
{{- $enabled := .integration.configuration.enableDeliveryPriceOverride | default "" | toString | trim | lower -}}
{{- if not (or (eq $enabled "true") (eq $enabled "yes")) -}}
    {{- stop "Overriding the shipping price is not enabled for this app. An admin must set \"Allow agents to override shipping price\" to true in the Skio app configuration first." -}}
{{- end -}}

{{- /* Confirmation checkbox: human-path friction, NOT the access control. */ -}}
{{- if ne (.inputs.confirmed | default false) true -}}
    {{- stop "Check the confirmation box to change this subscription's shipping price." -}}
{{- end -}}
https://graphql.skio.com/v1/graphql
