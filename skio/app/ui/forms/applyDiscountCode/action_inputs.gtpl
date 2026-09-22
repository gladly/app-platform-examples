{{- /* Form attrs are always strings on the wire, so Int/Float inputs convert here, guarded
       by a regex on the RAW string: sprig's `int` turns "abc" into 0 silently. */ -}}
{{- $out := dict -}}
{{- $vsubscriptionId := .subscriptionId | default "" | toString | trim -}}
{{- if ne $vsubscriptionId "" -}}{{- $out = set $out "subscriptionId" $vsubscriptionId -}}{{- end -}}
{{- $vcode := .code | default "" | toString | trim -}}
{{- if ne $vcode "" -}}{{- $out = set $out "code" $vcode -}}{{- end -}}
{{- /* Checkbox: the platform sends attrs as strings, so a ticked box arrives as "true"/"on"/"1",
       never as a JSON boolean. Emit a real boolean for the action, and omit the key entirely when
       unticked so the action's `ne (.inputs.confirmed | default false) true` guard stops. */ -}}
{{- $vconfirmed := .confirmed | default "" | toString | lower | trim -}}
{{- if or (eq $vconfirmed "true") (eq $vconfirmed "on") (eq $vconfirmed "1") -}}{{- $out = set $out "confirmed" true -}}{{- end -}}
{{ toJson $out }}
