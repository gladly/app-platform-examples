{{- /* A free gift can only be added to a QUEUED or ERROR charge (a processed charge -> 422), so
       filter the charges list to those statuses. Queued (the "next order") sorts first, then
       errored ("retrying") -- same customer-vocabulary status map as applyDiscount/removeDiscount.
       The agent then enters a Shopify variant id and a quantity (free text; quantity is coerced to
       an int in action_inputs.gtpl). */ -}}
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
  "title": "Add free gift",
{{- if $charges}}
  "submitButton": "Add gift",
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
        "placeholder": "Select the order to add the gift to.",
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
      "label": "Shopify variant ID",
      "attr": "externalVariantId",
      "input": { "type": "text", "placeholder": "e.g. 51139736109343", "optional": false },
      "hint": "The Shopify variant ID of the product to add as a free gift."
    },
    {
      "type": "input",
      "label": "Quantity",
      "attr": "quantity",
      "input": { "type": "text", "placeholder": "1", "optional": false }
    }
{{- else}}
    {
      "type": "text",
      "text": "This customer has no queued or errored charges that can take a free gift."
    }
{{- end}}
  ]
}
