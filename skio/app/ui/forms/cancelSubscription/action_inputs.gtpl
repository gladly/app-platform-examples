{{- /* Form attrs are always strings on the wire, so Int/Float inputs convert here, guarded
       by a regex on the RAW string: sprig's `int` turns "abc" into 0 silently. */ -}}
{{- $out := dict -}}
{{- $vsubscriptionId := .subscriptionId | default "" | toString | trim -}}
{{- if ne $vsubscriptionId "" -}}{{- $out = set $out "subscriptionId" $vsubscriptionId -}}{{- end -}}
{{- $vshouldSendNotif := .shouldSendNotif | default "" | toString | lower | trim -}}
{{- if or (eq $vshouldSendNotif "true") (eq $vshouldSendNotif "on") (eq $vshouldSendNotif "1") -}}{{- $out = set $out "shouldSendNotif" true -}}{{- end -}}
{{- $vinvokeSubscriptionCancelJourneys := .invokeSubscriptionCancelJourneys | default "" | toString | lower | trim -}}
{{- if or (eq $vinvokeSubscriptionCancelJourneys "true") (eq $vinvokeSubscriptionCancelJourneys "on") (eq $vinvokeSubscriptionCancelJourneys "1") -}}{{- $out = set $out "invokeSubscriptionCancelJourneys" true -}}{{- end -}}
{{- $vcancelSessionId := .cancelSessionId | default "" | toString | trim -}}
{{- if ne $vcancelSessionId "" -}}{{- $out = set $out "cancelSessionId" $vcancelSessionId -}}{{- end -}}
{{- $vpermanentlyCancel := .permanentlyCancel | default "" | toString | lower | trim -}}
{{- if or (eq $vpermanentlyCancel "true") (eq $vpermanentlyCancel "on") (eq $vpermanentlyCancel "1") -}}{{- $out = set $out "permanentlyCancel" true -}}{{- end -}}
{{- /* `confirmed` is the safety gate, so unlike the optional flags above it is ALWAYS emitted:
       an absent key and a false key both stop the action, but emitting it makes an unconfirmed
       submission visible in the action inputs instead of looking like a missing field.
       A checkbox arrives as a string like the rest of the form, so the same `toString` funnel
       that the flags above use is what normalises it; it also survives a real JSON bool,
       because `true | toString` is "true" and `false | toString` is "false". */ -}}
{{- $vconfirmed := .confirmed | default "" | toString | lower | trim -}}
{{- $out = set $out "confirmed" (or (eq $vconfirmed "true") (eq $vconfirmed "on") (eq $vconfirmed "1")) -}}
{{ toJson $out }}
