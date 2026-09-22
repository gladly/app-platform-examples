{{- /* KTD8 abuse guards run BEFORE the request is built, so a blocked change never reaches
       Recharge. Each guard calls `stop`, which surfaces the message to the agent as a failure. */ -}}

{{- /* Guard 1: require an explicit typed confirmation (case-insensitive, whitespace-tolerant). */ -}}
{{- $confirmation := .inputs.confirmationCopy | default "" | toString -}}
{{- if ne (lower (trim $confirmation)) "approve" -}}
    {{- stop "Type approve in the confirmation field to submit this price change." -}}
{{- end -}}

{{- /* Guard 2: the price must be a plain positive decimal. Validate the RAW string (not a
       float-parsed copy) so the value we cap-check is byte-for-byte the value we send. */ -}}
{{- $price := .inputs.price | default "" | toString -}}
{{- if not (regexMatch `^[0-9]+(\.[0-9]{1,2})?$` $price) -}}
    {{- stop (printf "Price %q must be a plain decimal with up to two places, e.g. 12.00 (no spaces, commas, sign, or exponent)." $price) -}}
{{- end -}}
{{- $priceFloat := $price | float64 -}}
{{- if le $priceFloat 0.0 -}}
    {{- stop "Price must be greater than zero." -}}
{{- end -}}

{{- /* Guard 3: enforce the merchant-configured maximum (fail closed if unset). */ -}}
{{- $capRaw := .integration.configuration.maxSubscriptionPrice | default "" | toString -}}
{{- if eq (trim $capRaw) "" -}}
    {{- stop "No maximum subscription price is configured for this app. An admin must set Maximum subscription price in the app configuration before agents can adjust price." -}}
{{- end -}}
{{- $cap := $capRaw | float64 -}}
{{- if gt $priceFloat $cap -}}
    {{- stop (printf "Price %s exceeds the configured maximum of %s. A manager must make a larger change directly in Recharge." $price $capRaw) -}}
{{- end -}}

{{- printf "https://api.rechargeapps.com/subscriptions/%s" .inputs.subscriptionId -}}
