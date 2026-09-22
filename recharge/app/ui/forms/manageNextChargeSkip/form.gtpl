{{- /* Skip or unskip a subscription's next charge -- one form, an Operation dropdown. The charge
       picker includes queued/error (skippable) AND already-skipped (unskippable) charges, labeled
       with their actual status, so the SAME list serves both operations regardless of which the
       agent picks -- Recharge itself rejects a mismatched pick (e.g. "skip" on an already-skipped
       charge) with a clear error, same as any other state-gated action in this app. Each option
       packs {chargeId, subscriptionId} as a JSON value (rescheduleNextCharge's idiom) since the
       action needs both: chargeId drives the URL, subscriptionId is the purchase_item_ids body.
       One option per (charge, subscription line item) pair -- a charge can bundle more than one
       subscription; a free-gift line item (null purchase_item_id) is skipped, not offered. */ -}}
{{- $opts := list}}
{{- if and .data .data.charges}}
  {{- range $c := .data.charges}}
    {{- $st := $c.status | toString}}
    {{- if or (eq $st "queued") (eq $st "error") (eq $st "skipped")}}
      {{- $date := default "n/a" $c.scheduled_at}}
      {{- $chargeLabel := ""}}
      {{- if eq $st "queued"}}
        {{- $chargeLabel = printf "Next order — ships %s (#%s)" $date $c.id}}
      {{- else if eq $st "error"}}
        {{- $chargeLabel = printf "Retrying order — was %s (#%s)" $date $c.id}}
      {{- else}}
        {{- $chargeLabel = printf "Skipped order — was %s (#%s)" $date $c.id}}
      {{- end}}
      {{- range $li := $c.line_items}}
        {{- if $li.purchase_item_id}}
          {{- $itemTitle := default "Subscription" $li.title}}
          {{- $label := printf "%s — %s" $chargeLabel $itemTitle}}
          {{- $opts = append $opts (dict "chargeId" $c.id "subscriptionId" $li.purchase_item_id "label" $label)}}
        {{- end}}
      {{- end}}
    {{- end}}
  {{- end}}
{{- end}}
{
  "title": "Skip or unskip next order",
{{- if $opts}}
  "submitButton": "Submit",
{{end -}}
  "closeButton": "Close",
  "sections": [
{{- if $opts}}
    {
      "type": "input",
      "label": "Operation",
      "attr": "operation",
      "input": {
        "type": "select",
        "placeholder": "Select skip or unskip",
        "options": [
          { "text": "Skip next order", "value": "skip" },
          { "text": "Unskip next order", "value": "unskip" }
        ],
        "optional": false
      }
    },
    {
      "type": "input",
      "label": "Charge",
      "attr": "chargeSelection",
      "input": {
        "type": "select",
        "placeholder": "Select the order",
        "options": [
        {{- range $i, $o := $opts}}
          {{- if gt $i 0}},{{end}}
          {
            "text": {{ $o.label | toJson }},
            {{- $value := dict "chargeId" $o.chargeId "subscriptionId" $o.subscriptionId}}
            "value": {{toJson $value | toJson}}
          }
        {{- end}}
        ],
        "optional": false
      }
    }
{{- else}}
    {
      "type": "text",
      "text": "This customer has no queued, retrying, or skipped orders to skip or unskip."
    }
{{- end}}
  ]
}
