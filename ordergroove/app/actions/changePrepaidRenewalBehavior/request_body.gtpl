{{- /* Ordergroove accepts exactly these three values, case-sensitive. The allowlist
       lives in the action because Team Assist calls actions without rendering the
       form, so a form dropdown constrains only one of the two callers. */ -}}
{{- $behavior := printf "%v" .inputs.renewalBehavior -}}
{{- if not (has $behavior (list "autorenew" "cancel" "downgrade")) -}}
  {{ stop (printf "Renewal behavior must be autorenew, cancel or downgrade (got %q)." $behavior) }}
{{- end -}}
{
  "renewal_behavior": "{{$behavior}}"
}
