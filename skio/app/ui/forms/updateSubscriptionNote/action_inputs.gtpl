{{- /* Form attrs are always strings on the wire, so Int/Float inputs convert here, guarded
       by a regex on the RAW string: sprig's `int` turns "abc" into 0 silently. */ -}}
{{- $out := dict -}}
{{- $vsubscriptionId := .subscriptionId | default "" | toString | trim -}}
{{- if ne $vsubscriptionId "" -}}{{- $out = set $out "subscriptionId" $vsubscriptionId -}}{{- end -}}
{{- /* `note` is deliberately NOT dropped when empty. The action reads an absent note as
       "leave it alone" and an empty note as "clear it", and Skio's UpdateSubscriptionNoteInput
       accepts an empty string to clear (verified against a live Skio sandbox). The form always
       renders the `note` attr, so a blank field must reach the action as "" - dropping it here
       would silently turn a clear into a no-op. Only a genuinely absent attr is omitted. */ -}}
{{- if hasKey . "note" -}}
    {{- $out = set $out "note" (.note | default "" | toString | trim) -}}
{{- end -}}
{{ toJson $out }}
