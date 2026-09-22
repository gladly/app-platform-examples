{{- /* Guards run here, not in the form - see "Merchant gates" in the app README. */ -}}

{{- /* A normal cancellation is reversible with reactivateSubscription. permanentlyCancel is not:
       it sets statusContext to PERMANENTLY_CANCELLED and the subscription can never come back.
       So the ordinary path stays frictionless and only the irreversible one is gated. */ -}}
{{- if eq .inputs.permanentlyCancel true -}}
    {{- $pc := .integration.configuration.enablePermanentCancel | default "" | toString | trim | lower -}}
    {{- if not (or (eq $pc "true") (eq $pc "yes")) -}}
        {{- stop "Permanent cancellation is not enabled for this app. An admin must set \"Allow agents to permanently cancel\" to true in the Skio app configuration. A normal cancellation is reversible and needs no approval." -}}
    {{- end -}}
    {{- if ne (.inputs.confirmed | default false) true -}}
        {{- stop "Check the confirmation box to PERMANENTLY cancel this subscription. This cannot be undone." -}}
    {{- end -}}
{{- end -}}
https://graphql.skio.com/v1/graphql
