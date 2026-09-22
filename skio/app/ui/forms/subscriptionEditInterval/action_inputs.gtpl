{{- /* Form attrs are always strings on the wire, so Int/Float inputs convert here.

       No range guard on the two interval counts: the action owns that rule. A form-side check
       protects only one of the two callers, because Gladly AI and Team Assist call
       subscriptionEditInterval directly and never render this form - the same reason the
       merchant gates, the interval enum checks and the "Nothing to change" guard live in the
       action. So convert and forward, and let the action's `stop` reject anything below 1
       with an agent-facing message that reaches every caller.

       Forwarding is what makes that possible. Sprig's `int` turns "abc" into 0 and "-5" into
       -5, so garbage and negatives arrive at the action as out-of-range numbers it rejects,
       instead of being dropped here and vanishing - a drop was a silent partial success, with
       the mutation going ahead on whatever else was filled in, so "MONTH, every 2" quietly
       became plain "MONTH" and the form still reported the frequency change as done.

       A blank field is still omitted entirely, so blank keeps meaning "leave this alone". */ -}}
{{- $out := dict -}}
{{- $vsubscriptionId := .subscriptionId | default "" | toString | trim -}}
{{- if ne $vsubscriptionId "" -}}{{- $out = set $out "subscriptionId" $vsubscriptionId -}}{{- end -}}
{{- $vbillingInterval := .billingInterval | default "" | toString | trim -}}
{{- if ne $vbillingInterval "" -}}{{- $out = set $out "billingInterval" $vbillingInterval -}}{{- end -}}
{{- $vbillingIntervalCount := .billingIntervalCount | default "" | toString | trim -}}
{{- if ne $vbillingIntervalCount "" -}}{{- $out = set $out "billingIntervalCount" ($vbillingIntervalCount | int) -}}{{- end -}}
{{- $vnextBillingDate := .nextBillingDate | default "" | toString | trim -}}
{{- if ne $vnextBillingDate "" -}}{{- $out = set $out "nextBillingDate" $vnextBillingDate -}}{{- end -}}
{{- $vprepaidDeliveryInterval := .prepaidDeliveryInterval | default "" | toString | trim -}}
{{- if ne $vprepaidDeliveryInterval "" -}}{{- $out = set $out "prepaidDeliveryInterval" $vprepaidDeliveryInterval -}}{{- end -}}
{{- $vprepaidDeliveryIntervalCount := .prepaidDeliveryIntervalCount | default "" | toString | trim -}}
{{- if ne $vprepaidDeliveryIntervalCount "" -}}{{- $out = set $out "prepaidDeliveryIntervalCount" ($vprepaidDeliveryIntervalCount | int) -}}{{- end -}}
{{- $vcaller := .caller | default "" | toString | trim -}}
{{- if ne $vcaller "" -}}{{- $out = set $out "caller" $vcaller -}}{{- end -}}
{{ toJson $out }}
