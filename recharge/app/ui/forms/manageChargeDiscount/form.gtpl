{{- /* Apply or remove a discount on a charge -- one form, an Operation dropdown (mirrors
       manageNextChargeSkip). The charge picker lists queued/error charges: queued (the "next order")
       sorts first, then errored ("retrying") -- customer vocabulary, not Recharge-internal status
       words. discountCode is used only when applying; it is ignored on remove, and Recharge rejects
       a remove-with-no-discount inline. */ -}}
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
  "title": "Apply or remove discount",
{{- if $charges}}
  "submitButton": "Submit",
{{end -}}
  "closeButton": "Close",
  "sections": [
{{- if $charges}}
    {
      "type": "input",
      "label": "Operation",
      "attr": "operation",
      "input": {
        "type": "select",
        "placeholder": "Select apply or remove",
        "options": [
          { "text": "Apply discount", "value": "apply" },
          { "text": "Remove discount", "value": "remove" }
        ],
        "optional": false
      }
    },
    {
      "type": "input",
      "label": "Charge",
      "attr": "chargeId",
      "input": {
        "type": "select",
        "placeholder": "Select the order",
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
    },
    {
      "type": "input",
      "label": "Discount code",
      "attr": "discountCode",
      "input": { "type": "text", "placeholder": "e.g. WELCOME10", "optional": true },
      "hint": "Required to apply a discount (case-sensitive — paste exactly, no spaces). Ignored when removing."
    }
{{- else}}
    {
      "type": "text",
      "text": "This customer has no queued or errored charges that can take a discount change."
    }
{{- end}}
  ]
}
