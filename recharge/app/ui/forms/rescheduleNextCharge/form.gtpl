{{- /* Cadence-relative delay options. Each active, non-prepaid subscription with an upcoming charge
       yields options of +1..N billing cycles, where N is the admin's maxDelayCycles (echoed onto the
       subscription by the pull, default 3). "N cycles" = next_charge + N x charge_interval_frequency
       in order_interval_unit. Derivation is deterministic (no `now`) so golden tests are stable. */ -}}
{{- $opts := list -}}
{{- $prepaidSeen := false -}}
{{- $max := 3 -}}
{{- if and .data .data.subscriptions (gt (len .data.subscriptions) 0) -}}
  {{- /* maxDelayCycles is echoed identically onto every subscription by the pull (agent forms
         can't read config), so read it from the first record. Clamp it: floor to 1 so a blank,
         zero, negative, or non-numeric admin value never collapses the list into a misleading
         empty state or yields past-dated options; ceil to 24 as a runaway guard so a typo (e.g.
         "100") can't flood the dropdown -- 24 cycles exceeds any realistic delay. */ -}}
  {{- $max = ((index .data.subscriptions 0).maxDelayCycles | default 3 | int) -}}
  {{- if lt $max 1 -}}{{- $max = 1 -}}{{- else if gt $max 24 -}}{{- $max = 24 -}}{{- end -}}
  {{- range $sub := .data.subscriptions -}}
    {{- if $sub.is_prepaid -}}
      {{- $prepaidSeen = true -}}
    {{- else if and (eq ($sub.status | toString) "active") $sub.has_queued_charges $sub.next_charge_scheduled_at -}}
      {{- /* Extract y/m/d as integers (substr 0 10 truncates a full RFC3339 timestamp to the date).
             toDate here only *reads* the fields -- no time arithmetic -- so the local-zone parse is
             safe. All date math below is pure integer + printf, never Sprig's date/dateModify, which
             format in the server's local zone and drift a day across a DST boundary for hour math. */ -}}
      {{- $base := toDate "2006-01-02" (substr 0 10 (toString $sub.next_charge_scheduled_at)) -}}
      {{- $y := $base | date "2006" | int -}}
      {{- $mo := $base | date "1" | int -}}
      {{- $d := $base | date "2" | int -}}
      {{- $unit := $sub.order_interval_unit | toString -}}
      {{- $step := $sub.charge_interval_frequency | int -}}
      {{- $title := default "Subscription" $sub.product_title -}}
      {{- range $i := until $max -}}
        {{- $n := add $i 1 -}}
        {{- $amount := mul $n $step -}}
        {{- $date := "" -}}
        {{- if eq $unit "month" -}}
          {{- /* month: calendar-correct add with day-clamp, since a month is not a fixed day count. */ -}}
          {{- $tm := add (sub $mo 1) $amount -}}
          {{- $ny := add $y (div $tm 12) -}}
          {{- $nm := add (mod $tm 12) 1 -}}
          {{- $dim := 31 -}}
          {{- if or (eq $nm 4) (eq $nm 6) (eq $nm 9) (eq $nm 11) -}}{{- $dim = 30 -}}{{- end -}}
          {{- if eq $nm 2 -}}
            {{- $leap := and (eq (mod $ny 4) 0) (or (ne (mod $ny 100) 0) (eq (mod $ny 400) 0)) -}}
            {{- $dim = (ternary 29 28 $leap) -}}
          {{- end -}}
          {{- $nd := (ternary $dim $d (gt $d $dim)) -}}
          {{- $date = printf "%04d-%02d-%02d" $ny $nm $nd -}}
        {{- else -}}
          {{- /* day / week: add whole days via Julian Day Number -- integer-only, so the result is
                 identical in every timezone (no DST drift). week = 7 days per cycle. */ -}}
          {{- $days := $amount -}}
          {{- if eq $unit "week" -}}{{- $days = mul $amount 7 -}}{{- end -}}
          {{- $a := div (sub 14 $mo) 12 -}}
          {{- $ya := sub (add $y 4800) $a -}}
          {{- $ma := sub (add $mo (mul 12 $a)) 3 -}}
          {{- $jdn := add $d (div (add (mul 153 $ma) 2) 5) -}}
          {{- $jdn = add $jdn (mul 365 $ya) -}}
          {{- $jdn = add $jdn (div $ya 4) -}}
          {{- $jdn = sub $jdn (div $ya 100) -}}
          {{- $jdn = add $jdn (div $ya 400) -}}
          {{- $jdn = sub $jdn 32045 -}}
          {{- $jdn = add $jdn $days -}}
          {{- $g := add $jdn 32044 -}}
          {{- $bb := div (add (mul 4 $g) 3) 146097 -}}
          {{- $cc := sub $g (div (mul 146097 $bb) 4) -}}
          {{- $dd := div (add (mul 4 $cc) 3) 1461 -}}
          {{- $ee := sub $cc (div (mul 1461 $dd) 4) -}}
          {{- $mm := div (add (mul 5 $ee) 2) 153 -}}
          {{- $nd := add (sub $ee (div (add (mul 153 $mm) 2) 5)) 1 -}}
          {{- $nm := sub (add $mm 3) (mul 12 (div $mm 10)) -}}
          {{- $ny := add (sub (add (mul 100 $bb) $dd) 4800) (div $mm 10) -}}
          {{- $date = printf "%04d-%02d-%02d" $ny $nm $nd -}}
        {{- end -}}
        {{- $unitLabel := $unit -}}
        {{- if ne $amount 1 -}}{{- $unitLabel = printf "%ss" $unit -}}{{- end -}}
        {{- $label := printf "+%d %s" $amount $unitLabel -}}
        {{- $opts = append $opts (dict "subscriptionId" $sub.id "resumeDate" $date "label" $label "title" $title) -}}
      {{- end -}}
    {{- end -}}
  {{- end -}}
{{- end -}}
{
  "title": "Delay next order",
{{- if $opts}}
  "submitButton": "Reschedule",
{{end -}}
  "closeButton": "Close",
  "sections": [
{{- if $opts}}
    {
      "type": "input",
      "label": "New next-charge date",
      "attr": "rescheduleSelection",
      "input": {
        "type": "select",
        "placeholder": "Select a subscription and a new charge date",
        "options": [
        {{- range $i, $o := $opts}}
          {{- if gt $i 0}},{{end}}
          {
            "text": {{ printf "%s: %s -> %s" $o.title $o.label $o.resumeDate | toJson }},
            {{- $value := dict "subscriptionId" $o.subscriptionId "resumeDate" $o.resumeDate}}
            "value": {{toJson $value | toJson}}
          }
        {{- end}}
        ],
        "optional": false
      }
    }
{{- else if $prepaidSeen}}
    {
      "type": "text",
      "text": "Prepaid subscriptions can't be delayed here."
    }
{{- else}}
    {
      "type": "text",
      "text": "This customer has no active subscriptions with an upcoming charge to reschedule."
    }
{{- end}}
  ]
}
