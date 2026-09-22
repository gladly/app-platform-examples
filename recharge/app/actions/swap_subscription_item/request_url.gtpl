{{- $subscriptionId := .inputs.subscriptionId -}}

{{- /* The optional price field is guarded exactly like adjust_subscription_price's KTD8 cap
       (same regex, same fail-closed cap check) so a caller can't bypass the price cap by setting
       it here instead. Only runs when price is actually supplied -- omitting it (the agent form's
       only path) skips the guard entirely, since no price is being set. */ -}}
{{- $price := .inputs.price | default "" | toString -}}
{{- if ne (trim $price) "" -}}
    {{- if not (regexMatch `^[0-9]+(\.[0-9]{1,2})?$` $price) -}}
        {{- stop (printf "Price %q must be a plain decimal with up to two places, e.g. 12.00 (no spaces, commas, sign, or exponent)." $price) -}}
    {{- end -}}
    {{- $priceFloat := $price | float64 -}}
    {{- if le $priceFloat 0.0 -}}
        {{- stop "Price must be greater than zero." -}}
    {{- end -}}
    {{- $capRaw := .integration.configuration.maxSubscriptionPrice | default "" | toString -}}
    {{- if eq (trim $capRaw) "" -}}
        {{- stop "No maximum subscription price is configured for this app. An admin must set Maximum subscription price in the app configuration before a price can be set here." -}}
    {{- end -}}
    {{- $cap := $capRaw | float64 -}}
    {{- if gt $priceFloat $cap -}}
        {{- stop (printf "Price %s exceeds the configured maximum of %s. A manager must make a larger change directly in Recharge." $price $capRaw) -}}
    {{- end -}}
{{- end -}}

{{- printf "https://api.rechargeapps.com/subscriptions/%s" $subscriptionId -}}
