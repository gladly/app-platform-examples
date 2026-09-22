{{- /* Store credit is a Recharge Plus/Custom feature; on plans without it the token cannot be
       granted the credit scopes and this endpoint returns 403 "insufficient permissions". The
       platform intercepts 401/403 as auth errors BEFORE response_transformation.gtpl runs (even
       with rawResponse), so that error can't be swallowed downstream -- and because data pulls run
       in parallel and any one erroring discards the WHOLE customer refresh, a credit 403 blanks the
       entire Recharge card (subscriptions, charges, addresses included). Store credit is therefore
       OPT-IN: this pull runs only when the admin checks configuration.enableStoreCredit. When it's
       off (the default) we emit NO URL (never `stop`, which would itself error the shared refresh):
       a pull that produces zero requests succeeds with no data. Accessed via `index` (not a dotted
       path) so the field stays optional for the upgrade compatibility checker. */ -}}
{{- if index .integration.configuration "enableStoreCredit" -}}
{{- $customerId := (first .externalData.recharge_customer).id -}}
{{- printf "https://api.rechargeapps.com/credit_accounts?customer_id=%s" $customerId -}}
{{- end -}}
