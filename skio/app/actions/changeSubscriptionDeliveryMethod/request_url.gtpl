{{- /* Guards run here, not in the form - see "Merchant gates" in the app README. */ -}}

{{- /* Skio accepts exactly three values and they are lowercase. Confirmed live 2026-09-08:
       SHIPPING, LOCAL_PICKUP, LOCAL_DELIVERY, localDelivery and delivery are all rejected
       with "Unknown delivery method type". Reject an unknown value here rather than letting
       Skio's raw error reach the agent, and never name a value Skio does not take. */ -}}
{{- $dmt := .inputs.deliveryMethodType | default "" | toString | trim | lower -}}
{{- if eq $dmt "" -}}
    {{- stop "A delivery method is required. Choose shipping, pickup or local_delivery." -}}
{{- end -}}
{{- if not (has $dmt (list "shipping" "pickup" "local_delivery")) -}}
    {{- stop (printf "%s is not a delivery method Skio accepts. Use shipping, pickup or local_delivery." $dmt) -}}
{{- end -}}
{{- /* Skio rejects these two combinations itself - "Pickup location ID is required for pickup
       delivery method" and "Phone number is required for local delivery method" - both
       confirmed live. Saying so here costs the agent one round trip instead of two. */ -}}
{{- if and (eq $dmt "pickup") (eq (.inputs.pickupLocationId | default "" | toString | trim) "") -}}
    {{- stop "Store pickup needs a pickup location. Add the Shopify location id and try again." -}}
{{- end -}}
{{- if and (eq $dmt "local_delivery") (eq (.inputs.localDeliveryPhone | default "" | toString | trim) "") -}}
    {{- stop "Local delivery needs a contact number for the driver. Add one and try again." -}}
{{- end -}}

{{- /* Changing the delivery PRICE alongside the method is a money change, so it rides the same
       merchant gate as setDeliveryPriceOverride. Changing only the method does not. */ -}}
{{- if or (ne .inputs.deliveryPrice nil) (eq .inputs.setOverride true) -}}
    {{- $dp := .integration.configuration.enableDeliveryPriceOverride | default "" | toString | trim | lower -}}
    {{- if not (or (eq $dp "true") (eq $dp "yes")) -}}
        {{- stop "Overriding the shipping price is not enabled for this app. An admin must set \"Allow agents to override shipping price\" to true in the Skio app configuration, or change the delivery method without a price." -}}
    {{- end -}}
    {{- if ne (.inputs.confirmed | default false) true -}}
        {{- stop "Check the confirmation box to change this subscription's shipping price." -}}
    {{- end -}}
{{- end -}}
https://graphql.skio.com/v1/graphql
