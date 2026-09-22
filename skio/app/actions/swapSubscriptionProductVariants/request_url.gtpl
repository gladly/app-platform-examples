{{- /* Guards run here, not in the form - see "Merchant gates" in the app README. */ -}}

{{- /* Pricing option is String on the wire, enum in Skio - reject a bad literal here. */ -}}
{{- if and (ne .inputs.pricingOption nil) (ne (.inputs.pricingOption | toString | trim) "") -}}
{{- $allowedpricingOption := list "SAME" "NEW_VARIANT_PRICE" "CUSTOM" -}}
{{- $vpricingOption := .inputs.pricingOption | toString | trim | upper -}}
{{- if not (has $vpricingOption $allowedpricingOption) -}}
    {{- stop (printf "Pricing option %q is not valid. Use one of: SAME, NEW_VARIANT_PRICE, CUSTOM." $vpricingOption) -}}
{{- end -}}
{{- end -}}

{{- /* newCustomPrice is a String, not a Float, so the `ne nil` idiom the numeric inputs use is
       wrong here: an agent who submits the form with the price field cleared sends "", which is
       not nil. Absent and empty-after-trim both mean "no custom price", so normalise once and
       let BOTH guards below read this value rather than .inputs.newCustomPrice - otherwise an
       empty string trips the merchant gate on a swap that needs no gate, and slips past the
       CUSTOM-needs-a-price check that exists to stop exactly that. */ -}}
{{- $customPrice := "" -}}
{{- if ne .inputs.newCustomPrice nil -}}
    {{- $customPrice = .inputs.newCustomPrice | toString | trim -}}
{{- end -}}

{{- /* A custom price moves money. Merchant-gated + an explicit confirmation, and only when
       a price is actually supplied - the ordinary catalog-priced path stays frictionless. */ -}}
{{- if ne $customPrice "" -}}
    {{- $po := .integration.configuration.enablePriceOverride | default "" | toString | trim | lower -}}
    {{- if not (or (eq $po "true") (eq $po "yes")) -}}
        {{- stop "Setting a custom price is not enabled for this app. An admin must set \"Allow agents to override prices\" to true in the Skio app configuration, or leave the price blank to use the catalog price." -}}
    {{- end -}}
    {{- if ne (.inputs.confirmed | default false) true -}}
        {{- stop "Check the confirmation box to swap at a custom price." -}}
    {{- end -}}
{{- end -}}

{{- /* CUSTOM without a price silently falls back to the variant's list price, which is exactly
       the surprise a price-locked subscriber must never get. */ -}}
{{- if and (ne .inputs.pricingOption nil) (eq (.inputs.pricingOption | toString | trim | upper) "CUSTOM") -}}
    {{- if eq $customPrice "" -}}
        {{- stop "Pricing option CUSTOM needs a custom price. Use SAME to keep the subscriber's locked price, or NEW_VARIANT_PRICE for the new variant's list price." -}}
    {{- end -}}
{{- end -}}
https://graphql.skio.com/v1/graphql
