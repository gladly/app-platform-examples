{{- /* Guards run here, not in the form - see "Merchant gates" in the app README. */ -}}

{{- /* Skip window unit is String on the wire, enum in Skio - reject a bad literal here. */ -}}
{{- if and (ne .inputs.skipUnit nil) (ne (.inputs.skipUnit | toString | trim) "") -}}
{{- $allowedskipUnit := list "DAY" "WEEK" "MONTH" "YEAR" -}}
{{- $vskipUnit := .inputs.skipUnit | toString | trim | upper -}}
{{- if not (has $vskipUnit $allowedskipUnit) -}}
    {{- stop (printf "Skip window unit %q is not valid. Use one of: DAY, WEEK, MONTH, YEAR." $vskipUnit) -}}
{{- end -}}
{{- end -}}

{{- /* Skip window length must be a whole number of 1 or more: a zero-unit skip is a no-op
       Skio still reports as success, a negative one runs backwards, and a fraction is only ever
       a smaller unit said ambiguously (DAY is the finest unit Skio offers). Explicit nil check,
       not truthiness - a supplied 0 must be rejected, not read as an absent value. */ -}}
{{- if ne .inputs.skipValue nil -}}
{{- $vskipValue := .inputs.skipValue | float64 -}}
{{- if or (lt $vskipValue 1.0) (ne $vskipValue (floor $vskipValue)) -}}
    {{- stop (printf "Skip window length %v is not valid. Enter a whole number of 1 or more, and use a smaller unit instead of a fraction - DAY is the finest unit Skio offers, so skip 10 with unit DAY rather than 1.5 with unit WEEK. Leave the unit and the length blank to skip exactly one cycle." $vskipValue) -}}
{{- end -}}
{{- end -}}
https://graphql.skio.com/v1/graphql
