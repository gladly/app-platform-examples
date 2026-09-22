{{- /* A form input is a string on the wire and Team Assist may send either a string or
       a float64. sprig's `int` turns "abc" into 0 SILENTLY, which on Frequency would
       quietly set the interval to zero. Regex the RAW string and stop loudly instead.
       This guard lives in the action, not the form: Team Assist invokes actions
       directly and never renders ui/forms/. */ -}}
{{- $every := printf "%v" .inputs.every -}}
{{- if not (regexMatch "^[0-9]+$" $every) -}}
  {{ stop (printf "Frequency must be a whole number greater than zero (got %q)." $every) }}
{{- end -}}
{{- if lt (int $every) 1 -}}
  {{ stop "Frequency must be greater than zero." }}
{{- end -}}
{{- /* A form input is a string on the wire and Team Assist may send either a string or
       a float64. sprig's `int` turns "abc" into 0 SILENTLY, which on Frequency period would
       quietly make the period invalid. Regex the RAW string and stop loudly instead.
       This guard lives in the action, not the form: Team Assist invokes actions
       directly and never renders ui/forms/. */ -}}
{{- $everyPeriod := printf "%v" .inputs.everyPeriod -}}
{{- if not (regexMatch "^[0-9]+$" $everyPeriod) -}}
  {{ stop (printf "Frequency period must be a whole number greater than zero (got %q)." $everyPeriod) }}
{{- end -}}
{{- if lt (int $everyPeriod) 1 -}}
  {{ stop "Frequency period must be greater than zero." }}
{{- end -}}
{{- /* every_period is a code, not a unit: 1=days, 2=weeks, 3=months, 4=years. */ -}}
{{- if not (has (int $everyPeriod) (list 1 2 3 4)) -}}
  {{ stop (printf "Frequency period must be 1 (days), 2 (weeks), 3 (months) or 4 (years); got %s." $everyPeriod) }}
{{- end -}}
{
  "every": {{int $every}},
  "every_period": {{int $everyPeriod}}
}
