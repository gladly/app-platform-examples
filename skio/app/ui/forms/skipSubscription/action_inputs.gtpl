{{- /* Form attrs are always strings on the wire, so Int/Float inputs convert here.

       No range guard on `skipValue`: the action owns that rule. A form-side check protects
       only one of the two callers, because Gladly AI and Team Assist call skipSubscription
       directly and never render this form - the same reason the merchant gates and the
       skipUnit enum check live in the action. So convert and forward, and let the action's
       `stop` reject anything below 1 with an agent-facing message that reaches every caller.

       Forwarding is what makes that possible. Sprig's `float64` turns "abc" into 0 and "-5"
       into -5, so garbage and negatives arrive at the action as out-of-range numbers it
       rejects, instead of being dropped here and vanishing - a drop would either mis-report
       the problem as a missing value or, with no unit chosen, quietly skip exactly one cycle
       instead of the window the agent asked for.

       A blank field is still omitted entirely, so blank keeps meaning blank and leaving both
       halves empty still means skip exactly one cycle. */ -}}
{{- $out := dict -}}
{{- $vsubscriptionId := .subscriptionId | default "" | toString | trim -}}
{{- if ne $vsubscriptionId "" -}}{{- $out = set $out "subscriptionId" $vsubscriptionId -}}{{- end -}}
{{- $vskipUnit := .skipUnit | default "" | toString | trim -}}
{{- if ne $vskipUnit "" -}}{{- $out = set $out "skipUnit" $vskipUnit -}}{{- end -}}
{{- $vskipValue := .skipValue | default "" | toString | trim -}}
{{- if ne $vskipValue "" -}}{{- $out = set $out "skipValue" ($vskipValue | float64) -}}{{- end -}}
{{- $vcaller := .caller | default "" | toString | trim -}}
{{- if ne $vcaller "" -}}{{- $out = set $out "caller" $vcaller -}}{{- end -}}
{{ toJson $out }}
