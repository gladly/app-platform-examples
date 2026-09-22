{{- /* Guards run here, not in the form - see "Merchant gates" in the app README. */ -}}

{{- /* A custom price moves money. Merchant-gated + an explicit confirmation, and only when
       a price is actually supplied - the ordinary catalog-priced path stays frictionless. */ -}}
{{- if ne .inputs.price nil -}}
    {{- $po := .integration.configuration.enablePriceOverride | default "" | toString | trim | lower -}}
    {{- if not (or (eq $po "true") (eq $po "yes")) -}}
        {{- stop "Setting a custom price is not enabled for this app. An admin must set \"Allow agents to override prices\" to true in the Skio app configuration, or leave the price blank to use the catalog price." -}}
    {{- end -}}
    {{- if ne (.inputs.confirmed | default false) true -}}
        {{- stop "Check the confirmation box to add this product at a custom price." -}}
    {{- end -}}
{{- end -}}
https://graphql.skio.com/v1/graphql
