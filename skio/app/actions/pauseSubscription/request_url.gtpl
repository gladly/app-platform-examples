{{- /* Guards run here, not in the form - see "Merchant gates" in the app README. */ -}}

{{- /* Pause window unit is String on the wire, enum in Skio - reject a bad literal here. */ -}}
{{- if and (ne .inputs.skipUnit nil) (ne (.inputs.skipUnit | toString | trim) "") -}}
{{- $allowedskipUnit := list "DAY" "WEEK" "MONTH" "YEAR" -}}
{{- $vskipUnit := .inputs.skipUnit | toString | trim | upper -}}
{{- if not (has $vskipUnit $allowedskipUnit) -}}
    {{- stop (printf "Pause window unit %q is not valid. Use one of: DAY, WEEK, MONTH, YEAR." $vskipUnit) -}}
{{- end -}}
{{- end -}}

{{- /* Pause window length must be a whole number of 1 or more: Skio answers ok:true to a
       zero-unit pause, runs a negative one backwards, and a fraction is only ever a smaller
       unit said ambiguously (DAY is the finest unit Skio offers). Explicit nil check, not
       truthiness - a supplied 0 must be rejected, not read as an absent (open-ended) pause. */ -}}
{{- if ne .inputs.skipValue nil -}}
{{- $vskipValue := .inputs.skipValue | float64 -}}
{{- if or (lt $vskipValue 1.0) (ne $vskipValue (floor $vskipValue)) -}}
    {{- stop (printf "Pause window length %v is not valid. Enter a whole number of 1 or more, and use a smaller unit instead of a fraction - DAY is the finest unit Skio offers, so pause for 10 with unit DAY rather than 1.5 with unit WEEK. Leave the unit and the length blank for an open-ended pause." $vskipValue) -}}
{{- end -}}
{{- end -}}
https://graphql.skio.com/v1/graphql
