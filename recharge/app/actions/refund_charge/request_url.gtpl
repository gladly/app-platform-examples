{{- /* KTD8 abuse guards run BEFORE the request is built, so a blocked refund never reaches
       Recharge. Each guard calls `stop`, which surfaces the message to the agent as a failure.
       Mirrors adjust_credit / adjust_subscription_price: typed confirmation + raw-string decimal
       validation + a merchant-configured maximum, all fail-closed. */ -}}

{{- /* Guard 1: require an explicit typed confirmation (case-insensitive, whitespace-tolerant). */ -}}
{{- $confirmation := .inputs.confirmationCopy | default "" | toString -}}
{{- if ne (lower (trim $confirmation)) "approve" -}}
    {{- stop "Type approve in the confirmation field to submit this refund." -}}
{{- end -}}

{{- /* Guard 2: exactly one of amount (partial) or fullRefund (full) must be supplied. fullRefund is
       a "true"/"false" string (see the schema note) so compare as a string. */ -}}
{{- $amount := .inputs.amount | default "" | toString | trim -}}
{{- $isFull := eq (.inputs.fullRefund | default "" | toString | lower | trim) "true" -}}
{{- if and $isFull (ne $amount "") -}}
    {{- stop "Choose either a partial amount or a full refund, not both." -}}
{{- end -}}
{{- if and (not $isFull) (eq $amount "") -}}
    {{- stop "Enter a refund amount, or choose a full refund." -}}
{{- end -}}

{{- /* $effective is the dollars that will actually leave: the typed amount for a partial refund, or
       the charge's remaining refundable balance (supplied by the form) for a full refund. Validate
       the RAW string (not a float-parsed copy) so the value we cap-check is byte-for-byte what could
       be refunded: Sprig's float64 silently coerces whitespace/comma/sign/exponent strings under the
       cap (same reasoning as adjust_credit). */ -}}
{{- $effective := $amount -}}
{{- if $isFull -}}
    {{- $effective = .inputs.refundableAmount | default "" | toString | trim -}}
    {{- if eq $effective "" -}}
        {{- stop "This charge's refundable balance is unknown, so a full refund can't be capped. Refund a specific amount instead, or have a manager refund it in Recharge." -}}
    {{- end -}}
{{- end -}}
{{- if not (regexMatch `^[0-9]+(\.[0-9]{1,2})?$` $effective) -}}
    {{- stop (printf "Refund amount %q must be a plain decimal with up to two places, e.g. 10.00 (no spaces, commas, sign, or exponent)." $effective) -}}
{{- end -}}
{{- $effectiveFloat := $effective | float64 -}}
{{- if le $effectiveFloat 0.0 -}}
    {{- stop "Refund amount must be greater than zero." -}}
{{- end -}}

{{- /* Guard 3: enforce the merchant-configured maximum (fail closed if unset, with a message that
       names the real cause so the agent doesn't think the amount itself was the problem). */ -}}
{{- $capRaw := .integration.configuration.maxRefundAmount | default "" | toString -}}
{{- if eq (trim $capRaw) "" -}}
    {{- stop "No maximum refund amount is configured for this app. An admin must set Maximum refund amount in the app configuration before agents can refund a charge." -}}
{{- end -}}
{{- $cap := $capRaw | float64 -}}
{{- if gt $effectiveFloat $cap -}}
    {{- stop (printf "Refund amount %s exceeds the configured maximum of %s. A manager must make a larger refund directly in Recharge." $effective $capRaw) -}}
{{- end -}}

{{- printf "https://api.rechargeapps.com/charges/%s/refund" .inputs.chargeId -}}
