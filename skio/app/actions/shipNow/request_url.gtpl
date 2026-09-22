{{- /* Guards run here, not in the form - see "Merchant gates" in the app README. */ -}}

{{- /* Merchant opt-in, fail closed when unset. This gate used to live in request_body.gtpl,
       which made it the one gate of the seven that no expected_request_url.txt could assert.
       All seven now sit in request_url.gtpl and are covered by a blocked-state dataset. */ -}}
{{- $enabled := .integration.configuration.enableShipNow | default "" | toString | trim | lower -}}
{{- if not (or (eq $enabled "true") (eq $enabled "yes")) -}}
    {{- stop "Shipping the next order immediately is not enabled for this app. An admin must set \"Allow AI and agents to ship the next order now\" to true in the Skio app configuration first." -}}
{{- end -}}
https://graphql.skio.com/v1/graphql
