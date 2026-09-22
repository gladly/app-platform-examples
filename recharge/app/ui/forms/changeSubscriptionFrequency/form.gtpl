{{- /* Combined subscription x frequency-preset select. Each preset carries a matching
       (order_interval == charge_interval) triple so the agent can never submit a mismatch
       (which Recharge rejects as "regular -> prepaid"). */ -}}
{{- $presets := list
      (dict "label" "Weekly" "oif" 1 "unit" "week" "cif" 1)
      (dict "label" "Every 2 weeks" "oif" 2 "unit" "week" "cif" 2)
      (dict "label" "Every 4 weeks" "oif" 4 "unit" "week" "cif" 4)
      (dict "label" "Monthly" "oif" 1 "unit" "month" "cif" 1)}}
{{- $opts := list}}
{{- if and .data .data.subscriptions}}
  {{- range .data.subscriptions}}
    {{- if eq (.status | toString) "active"}}
      {{- $sub := .}}
      {{- range $p := $presets}}
        {{- $opts = append $opts (dict "subscriptionId" $sub.id "productTitle" (default "Subscription" $sub.product_title) "label" $p.label "oif" $p.oif "unit" $p.unit "cif" $p.cif)}}
      {{- end}}
    {{- end}}
  {{- end}}
{{- end}}
{
  "title": "Change subscription frequency",
{{- if $opts}}
  "submitButton": "Change frequency",
{{end -}}
  "closeButton": "Close",
  "sections": [
{{- if $opts}}
    {
      "type": "input",
      "label": "Frequency",
      "attr": "frequencySelection",
      "input": {
        "type": "select",
        "placeholder": "Select a subscription and new frequency",
        "options": [
        {{- range $i, $o := $opts}}
          {{- if gt $i 0}},{{end}}
          {
            "text": {{ printf "%s: %s" $o.productTitle $o.label | toJson }},
            {{- $value := dict "subscriptionId" $o.subscriptionId "orderIntervalFrequency" $o.oif "orderIntervalUnit" $o.unit "chargeIntervalFrequency" $o.cif "label" $o.label}}
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
      "text": "This customer has no active subscriptions to modify."
    }
{{- end}}
  ]
}
