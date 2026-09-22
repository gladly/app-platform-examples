{{- /* Guards run here, not in the form - see "Merchant gates" in the app README. */ -}}

{{- /* Billing interval is String on the wire, enum in Skio - reject a bad literal here. */ -}}
{{- if and (ne .inputs.billingInterval nil) (ne (.inputs.billingInterval | toString | trim) "") -}}
{{- $allowedbillingInterval := list "DAY" "WEEK" "MONTH" "YEAR" -}}
{{- $vbillingInterval := .inputs.billingInterval | toString | trim | upper -}}
{{- if not (has $vbillingInterval $allowedbillingInterval) -}}
    {{- stop (printf "Billing interval %q is not valid. Use one of: DAY, WEEK, MONTH, YEAR." $vbillingInterval) -}}
{{- end -}}
{{- end -}}

{{- /* Prepaid delivery interval is String on the wire, enum in Skio - reject a bad literal here. */ -}}
{{- if and (ne .inputs.prepaidDeliveryInterval nil) (ne (.inputs.prepaidDeliveryInterval | toString | trim) "") -}}
{{- $allowedprepaidDeliveryInterval := list "DAY" "WEEK" "MONTH" "YEAR" -}}
{{- $vprepaidDeliveryInterval := .inputs.prepaidDeliveryInterval | toString | trim | upper -}}
{{- if not (has $vprepaidDeliveryInterval $allowedprepaidDeliveryInterval) -}}
    {{- stop (printf "Prepaid delivery interval %q is not valid. Use one of: DAY, WEEK, MONTH, YEAR." $vprepaidDeliveryInterval) -}}
{{- end -}}
{{- end -}}

{{- /* Interval counts must be a whole number of 1 or more - zero is not a frequency, and a
       negative count is meaningless to Skio. Explicit nil checks, not truthiness: a supplied 0
       has to be rejected here rather than read as "no change" (the trap called out in
       data/pull/subscriptions/response_transformation.gtpl). */ -}}
{{- if ne .inputs.billingIntervalCount nil -}}
{{- $vbillingIntervalCount := .inputs.billingIntervalCount | float64 -}}
{{- if or (lt $vbillingIntervalCount 1.0) (ne $vbillingIntervalCount (floor $vbillingIntervalCount)) -}}
    {{- stop (printf "Billing interval count %v is not valid. Enter a whole number of 1 or more - MONTH with 2 bills every 2 months - or leave the count blank to keep the current frequency." $vbillingIntervalCount) -}}
{{- end -}}
{{- end -}}

{{- if ne .inputs.prepaidDeliveryIntervalCount nil -}}
{{- $vprepaidDeliveryIntervalCount := .inputs.prepaidDeliveryIntervalCount | float64 -}}
{{- if or (lt $vprepaidDeliveryIntervalCount 1.0) (ne $vprepaidDeliveryIntervalCount (floor $vprepaidDeliveryIntervalCount)) -}}
    {{- stop (printf "Prepaid delivery interval count %v is not valid. Enter a whole number of 1 or more - MONTH with 1 delivers every month - or leave the count blank to keep the current cadence." $vprepaidDeliveryIntervalCount) -}}
{{- end -}}
{{- end -}}

{{- /* An interval and its count require each other, in both directions. request_body.gtpl sends
       whichever half was supplied, so a lone interval leaves the old count behind (Skio reads
       the missing count as 1, turning "every 2 months" into monthly) and a lone count keeps the
       old unit - a silent partial change the caller is told succeeded. */ -}}
{{- $hasBillingInterval := and (ne .inputs.billingInterval nil) (ne (.inputs.billingInterval | toString | trim) "") -}}
{{- $hasBillingIntervalCount := ne .inputs.billingIntervalCount nil -}}
{{- if ne $hasBillingInterval $hasBillingIntervalCount -}}
    {{- stop "Changing the billing frequency needs both the billing interval and the interval count. Enter both - MONTH with 2 for every 2 months - or leave both blank to keep the current frequency." -}}
{{- end -}}
{{- $hasPrepaidInterval := and (ne .inputs.prepaidDeliveryInterval nil) (ne (.inputs.prepaidDeliveryInterval | toString | trim) "") -}}
{{- $hasPrepaidIntervalCount := ne .inputs.prepaidDeliveryIntervalCount nil -}}
{{- if ne $hasPrepaidInterval $hasPrepaidIntervalCount -}}
    {{- stop "Changing the prepaid delivery cadence needs both the prepaid delivery interval and its interval count. Enter both - MONTH with 1 for a delivery every month - or leave both blank to keep the current cadence." -}}
{{- end -}}

{{- if not (or (and (ne .inputs.billingInterval nil) (ne (.inputs.billingInterval | toString | trim) "")) (and (ne .inputs.billingIntervalCount nil) (ne (.inputs.billingIntervalCount | toString | trim) "")) (and (ne .inputs.nextBillingDate nil) (ne (.inputs.nextBillingDate | toString | trim) "")) (and (ne .inputs.prepaidDeliveryInterval nil) (ne (.inputs.prepaidDeliveryInterval | toString | trim) "")) (and (ne .inputs.prepaidDeliveryIntervalCount nil) (ne (.inputs.prepaidDeliveryIntervalCount | toString | trim) ""))) -}}
    {{- stop "Nothing to change. Supply at least a billing interval, an interval count, or a next billing date." -}}
{{- end -}}
https://graphql.skio.com/v1/graphql
