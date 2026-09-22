{{- /* Form attrs are always strings on the wire, so Int/Float inputs convert here, guarded
       by a regex on the RAW string: sprig's `int` turns "abc" into 0 silently. */ -}}
{{- $out := dict -}}
{{- $vsubscriptionId := .subscriptionId | default "" | toString | trim -}}
{{- if ne $vsubscriptionId "" -}}{{- $out = set $out "subscriptionId" $vsubscriptionId -}}{{- end -}}
{{- $visTemporaryPause := .isTemporaryPause | default "" | toString | lower | trim -}}
{{- if or (eq $visTemporaryPause "true") (eq $visTemporaryPause "on") (eq $visTemporaryPause "1") -}}{{- $out = set $out "isTemporaryPause" true -}}{{- end -}}
{{ toJson $out }}
