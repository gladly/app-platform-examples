{{- /* Form attrs are always strings on the wire, so Int/Float inputs convert here, guarded
       by a regex on the RAW string: sprig's `int` turns "abc" into 0 silently. */ -}}
{{- $out := dict -}}
{{- $vemail := .email | default "" | toString | trim -}}
{{- if ne $vemail "" -}}{{- $out = set $out "email" $vemail -}}{{- end -}}
{{- $vreturnToPath := .returnToPath | default "" | toString | trim -}}
{{- if ne $vreturnToPath "" -}}{{- $out = set $out "returnToPath" $vreturnToPath -}}{{- end -}}
{{ toJson $out }}
