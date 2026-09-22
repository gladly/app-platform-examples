{{- /* Remove a discount from a charge (mirrors applyDiscount, minus the code field). Queued (the
       "next order") sorts first, then errored ("retrying"). Removal is non-idempotent -- removing
       when none is applied surfaces server-side as a red inline error, which is acceptable for v1. */ -}}
{{- $queued := list}}
{{- $errored := list}}
{{- if and .data .data.charges}}
  {{- range .data.charges}}
    {{- $st := .status | toString}}
    {{- if eq $st "queued"}}{{- $queued = append $queued .}}
    {{- else if eq $st "error"}}{{- $errored = append $errored .}}
    {{- end}}
  {{- end}}
{{- end}}
{{- $charges := list}}
{{- range $queued}}{{- $charges = append $charges .}}{{- end}}
{{- range $errored}}{{- $charges = append $charges .}}{{- end}}
{
  "title": "Remove discount",
{{- if $charges}}
  "submitButton": "Remove discount",
{{end -}}
  "closeButton": "Close",
  "sections": [
{{- if $charges}}
    {
      "type": "input",
      "label": "Charge",
      "attr": "chargeId",
      "input": {
        "type": "select",
        "placeholder": "Select the order to remove the discount from.",
        "options": [
        {{- range $i, $c := $charges}}
          {{- if gt $i 0}},{{end}}
          {{- $st := $c.status | toString}}
          {{- $date := default "n/a" $c.scheduled_at}}
          {{- $label := ""}}
          {{- if eq $st "queued"}}
            {{- $label = printf "Next order — ships %s (#%s)" $date $c.id}}
          {{- else}}
            {{- $label = printf "Retrying order — was %s (#%s)" $date $c.id}}
          {{- end}}
          {{- if gt (default 0 $c.total_discounts | float64) 0.0}}
            {{- $label = printf "%s, has discount" $label}}
          {{- end}}
          {
            "text": {{ $label | toJson }},
            "value": {{ $c.id | toJson }}
          }
        {{- end}}
        ],
        "optional": false
      }
    }
{{- if and (eq (len $queued) 1) (eq (len $errored) 0)}},
    {
      "type": "text",
      "text": {{ printf "This removes any discount from the next order, scheduled %s." (default "n/a" (index $queued 0).scheduled_at) | toJson }}
    }
{{- end}}
{{- else}}
    {
      "type": "text",
      "text": "This customer has no queued or errored charges that could have a discount."
    }
{{- end}}
  ]
}
