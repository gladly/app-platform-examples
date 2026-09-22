{{- /* Guards run here, not in the form - see "Merchant gates" in the app README. */ -}}

{{- /* A custom price on ANY item is a price override. Merchant-gated + an explicit
       confirmation. */ -}}
{{- $wantsOverride := false -}}
{{- range $item := .inputs.items -}}
    {{- if ne $item.price nil -}}{{- $wantsOverride = true -}}{{- end -}}
    {{- if ne $item.currentPrice nil -}}{{- $wantsOverride = true -}}{{- end -}}
{{- end -}}
{{- if $wantsOverride -}}
    {{- $po := .integration.configuration.enablePriceOverride | default "" | toString | trim | lower -}}
    {{- if not (or (eq $po "true") (eq $po "yes")) -}}
        {{- stop "Setting a custom price is not enabled for this app. An admin must set \"Allow agents to override prices\" to true in the Skio app configuration, or leave every price blank to use catalog prices." -}}
    {{- end -}}
    {{- if ne (.inputs.confirmed | default false) true -}}
        {{- stop "Check the confirmation box to set a custom price." -}}
    {{- end -}}
{{- end -}}
https://graphql.skio.com/v1/graphql
