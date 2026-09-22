{{- /* Guards run here, not in the form - see "Merchant gates" in the app README. */ -}}

{{- if not (or (and (ne .inputs.subscriptionLineId nil) (ne (.inputs.subscriptionLineId | toString | trim) "")) (and (ne .inputs.prepaidSubscriptionLineId nil) (ne (.inputs.prepaidSubscriptionLineId | toString | trim) ""))) -}}
    {{- stop "This action is keyed by the subscription LINE, not the subscription. Supply a subscription line id (or a prepaid subscription line id)." -}}
{{- end -}}

{{- /* A custom price moves money. Merchant-gated + an explicit confirmation, and only when
       a price is actually supplied - the ordinary catalog-priced path stays frictionless. */ -}}
{{- if ne .inputs.currentPrice nil -}}
    {{- $po := .integration.configuration.enablePriceOverride | default "" | toString | trim | lower -}}
    {{- if not (or (eq $po "true") (eq $po "yes")) -}}
        {{- stop "Setting a custom price is not enabled for this app. An admin must set \"Allow agents to override prices\" to true in the Skio app configuration, or leave the price blank to use the catalog price." -}}
    {{- end -}}
    {{- if ne (.inputs.confirmed | default false) true -}}
        {{- stop "Check the confirmation box to set a custom price on this line." -}}
    {{- end -}}
{{- end -}}

{{- if not (or (ne .inputs.quantity nil) (ne .inputs.currentPrice nil) (and (ne .inputs.productVariantId nil) (ne (.inputs.productVariantId | toString | trim) "")) (and (ne .inputs.titleOverride nil) (ne (.inputs.titleOverride | toString | trim) ""))) -}}
    {{- stop "Nothing to change. Supply a quantity, a product variant, a price, or a display title." -}}
{{- end -}}
https://graphql.skio.com/v1/graphql
