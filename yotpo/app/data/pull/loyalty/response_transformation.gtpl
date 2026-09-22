{{- /* Yotpo's loyalty v3 endpoint has two "no loyalty profile" responses for an
       email that is a store customer but not enrolled in the loyalty program: a
       404 ("Could not find customer") and, for some shoppers, a 400. Neither is
       a real error. rawResponse is enabled in config.json so this transform runs
       on non-200 responses too; on a 400 or 404 we return an empty result (the
       loyalty card is then hidden) instead of letting the status error every
       card in the app. Auth failures surface as 401/403, which the platform
       intercepts before this transform runs, so treating 400 as "no data" here
       cannot mask an auth problem. */ -}}
{{- if or (eq .response.statusCode 404) (eq .response.statusCode 400) -}}
[]
{{- else if eq .response.statusCode 200 -}}
{{- $result := list -}}
{{- $body := fromJson (.response.body | toString) -}}
{{- if $body.customers -}}
{{- $loyalty := $body.customers -}}
{{- $transformedLoyalty := dict -}}
{{- $_ := set $transformedLoyalty "email" $loyalty.email -}}
{{- $_ := set $transformedLoyalty "points_balance" $loyalty.points_balance -}}
{{- $_ := set $transformedLoyalty "points_earned" $loyalty.points_earned -}}
{{- $_ := set $transformedLoyalty "last_seen_at" $loyalty.last_seen_at -}}
{{- $_ := set $transformedLoyalty "third_party_id" $loyalty.third_party_id -}}
{{- $_ := set $transformedLoyalty "pos_account_id" $loyalty.pos_account_id -}}
{{- $_ := set $transformedLoyalty "has_store_account" $loyalty.has_store_account -}}
{{- $_ := set $transformedLoyalty "credit_balance" $loyalty.credit_balance -}}
{{- $_ := set $transformedLoyalty "credit_balance_in_customer_currency" $loyalty.credit_balance_in_customer_currency -}}
{{- $_ := set $transformedLoyalty "opt_in" $loyalty.opt_in -}}
{{- $_ := set $transformedLoyalty "opted_in_at" $loyalty.opted_in_at -}}
{{- $_ := set $transformedLoyalty "points_expire_at" $loyalty.points_expire_at -}}
{{- if $loyalty.next_points_expire_on -}}
{{- $_ := set $transformedLoyalty "next_points_expire_on" (printf "%sT00:00:00Z" (trimSuffix "Z" $loyalty.next_points_expire_on)) -}}
{{- end -}}
{{- $_ := set $transformedLoyalty "next_points_expire_amount" $loyalty.next_points_expire_amount -}}
{{- $_ := set $transformedLoyalty "total_spend_cents" $loyalty.total_spend_cents -}}
{{- $_ := set $transformedLoyalty "total_purchases" $loyalty.total_purchases -}}
{{- $_ := set $transformedLoyalty "perks_redeemed" $loyalty.perks_redeemed -}}
{{- $_ := set $transformedLoyalty "last_purchase_at" $loyalty.last_purchase_at -}}
{{- $_ := set $transformedLoyalty "first_name" $loyalty.first_name -}}
{{- $_ := set $transformedLoyalty "last_name" $loyalty.last_name -}}
{{- $_ := set $transformedLoyalty "phone_number" $loyalty.phone_number -}}
{{- $_ := set $transformedLoyalty "vip_tier_name" (or $loyalty.vip_tier_name "") -}}
{{- $_ := set $transformedLoyalty "vip_tier_entry_date" $loyalty.vip_tier_entry_date -}}
{{- $_ := set $transformedLoyalty "vip_tier_expiration" $loyalty.vip_tier_expiration -}}
{{- $_ := set $transformedLoyalty "birthday_month" $loyalty.birthday_month -}}
{{- $_ := set $transformedLoyalty "birth_day" $loyalty.birth_day -}}
{{- $_ := set $transformedLoyalty "birthday_year" $loyalty.birthday_year -}}
{{- $_ := set $transformedLoyalty "vip_tier_actions_completed" $loyalty.vip_tier_actions_completed -}}
{{- $_ := set $transformedLoyalty "vip_tier_maintenance_requirements" $loyalty.vip_tier_maintenance_requirements -}}
{{- $_ := set $transformedLoyalty "vip_tier_upgrade_requirements" $loyalty.vip_tier_upgrade_requirements -}}
{{- $_ := set $transformedLoyalty "referral_code" $loyalty.referral_code -}}
{{- if $loyalty.referral_link -}}
{{- $transformedReferralLink := dict -}}
{{- $_ := set $transformedReferralLink "code" $loyalty.referral_link.code -}}
{{- if $loyalty.referral_link.customer_id -}}
{{- $_ := set $transformedReferralLink "customer_id" (printf "%.0f" $loyalty.referral_link.customer_id) -}}
{{- end -}}
{{- $_ := set $transformedReferralLink "expired" $loyalty.referral_link.expired -}}
{{- $_ := set $transformedReferralLink "link" $loyalty.referral_link.link -}}
{{- $_ := set $transformedLoyalty "referral_link" $transformedReferralLink -}}
{{- end -}}
{{- $result = append $result $transformedLoyalty -}}
{{- end -}}
{{- toJson $result -}}
{{- else -}}
{{- fail (printf "unexpected response status %d from Yotpo loyalty" (int .response.statusCode)) -}}
{{- end -}}
