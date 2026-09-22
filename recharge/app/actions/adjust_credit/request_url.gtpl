{{- /* KTD8 abuse guards run BEFORE the request is built, so a blocked adjustment never reaches
       Recharge. Each guard calls `stop`, which surfaces the message to the agent as a failure. */ -}}

{{- /* Guard 1: require an explicit typed confirmation (case-insensitive, whitespace-tolerant). */ -}}
{{- $confirmation := .inputs.confirmationCopy | default "" | toString -}}
{{- if ne (lower (trim $confirmation)) "approve" -}}
    {{- stop "Type approve in the confirmation field to submit this credit adjustment." -}}
{{- end -}}

{{- /* Guard 2: the amount must be a plain positive decimal. We validate the RAW string (not a
       float-parsed copy) so the value we cap-check is byte-for-byte the value we send: Sprig's
       float64 silently coerces whitespace-padded/comma/sign/exponent strings (e.g. "  100000  ")
       to a value that can slip under the cap while Recharge trims-and-accepts the raw string. */ -}}
{{- $amount := .inputs.amount | default "" | toString -}}
{{- if not (regexMatch `^[0-9]+(\.[0-9]{1,2})?$` $amount) -}}
    {{- stop (printf "Amount %q must be a plain decimal with up to two places, e.g. 10.00 (no spaces, commas, sign, or exponent)." $amount) -}}
{{- end -}}
{{- $amountFloat := $amount | float64 -}}
{{- if le $amountFloat 0.0 -}}
    {{- stop "Amount must be greater than zero." -}}
{{- end -}}

{{- /* Guard 3: enforce the merchant-configured maximum (fail closed if unset, with a message that
       names the real cause so the agent doesn't think the amount itself was the problem). */ -}}
{{- $capRaw := .integration.configuration.maxCreditAdjustment | default "" | toString -}}
{{- if eq (trim $capRaw) "" -}}
    {{- stop "No maximum credit adjustment is configured for this app. An admin must set Maximum credit adjustment in the app configuration before agents can adjust credit." -}}
{{- end -}}
{{- $cap := $capRaw | float64 -}}
{{- if gt $amountFloat $cap -}}
    {{- stop (printf "Adjustment amount %s exceeds the configured maximum of %s. A manager must make a larger adjustment directly in Recharge." $amount $capRaw) -}}
{{- end -}}

{{- printf "https://api.rechargeapps.com/credit_accounts/%s/credit_adjustments" .inputs.creditAccountId -}}
