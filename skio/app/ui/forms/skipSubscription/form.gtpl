{{- /* One option per eligible subscription. The label leads with the product the
       customer would name, not the uuid - the uuid is only ever the value. */ -}}
{{- $options := list -}}
{{- range $s := (default list .data.subscriptions) -}}
    {{- $status := $s.status | default "" | toString | upper -}}
    {{- $context := $s.statusContext | default "" | toString | upper -}}
    {{- if eq $status "ACTIVE" -}}
        {{- $name := "Subscription" -}}
        {{- $lines := default list $s.SubscriptionLines -}}
        {{- if gt (len $lines) 0 -}}
            {{- $line := index $lines 0 -}}
            {{- if and (ne $line.titleOverride nil) (ne ($line.titleOverride | toString) "") -}}
                {{- $name = $line.titleOverride | toString -}}
            {{- else -}}
                {{- $pv := $line.ProductVariant -}}
                {{- if kindIs "map" $pv -}}
                    {{- $product := "" -}}
                    {{- if kindIs "map" $pv.Product -}}
                        {{- $product = $pv.Product.title | default "" | toString -}}
                    {{- end -}}
                    {{- $name = trim (printf "%s %s" $product ($pv.title | default "" | toString)) -}}
                {{- end -}}
            {{- end -}}
            {{- if gt (len $lines) 1 -}}
                {{- $name = printf "%s +%d more" $name (sub (len $lines) 1) -}}
            {{- end -}}
        {{- end -}}
        {{- $label := printf "%s - %s" $name $status -}}
        {{- if ne $context "" -}}{{- $label = printf "%s (%s)" $label $context -}}{{- end -}}
        {{- if and (ne $s.nextBillingDate nil) (ne ($s.nextBillingDate | toString) "") -}}
            {{- $label = printf "%s - next %s" $label ($s.nextBillingDate | toString) -}}
        {{- end -}}
        {{- $options = append $options (dict "text" $label "value" ($s.id | toString)) -}}
    {{- end -}}
{{- end -}}
{
  "title": "Skip next order",
{{- if gt (len $options) 0}}
  "submitButton": "Skip next order",
{{end -}}
  "closeButton": "Close",
  "sections": [
{{- if gt (len $options) 0}}
    {
      "type": "input",
      "label": "Subscription",
      "attr": "subscriptionId",
      "input": {
        "type": "select",
        "placeholder": "Please select a subscription",
        "options": [
        {{- range $i, $o := $options}}
          {{- if gt $i 0}},{{end}}
          { "text": {{ $o.text | toJson }}, "value": {{ $o.value | toJson }} }
        {{- end}}
        ],
        "optional": false
      }
    },
    {
      "type": "input",
      "label": "Skip window unit",
      "attr": "skipUnit",
      "input": {
        "type": "select",
        "placeholder": "Please select a unit",
        "options": [
          { "text": "Day", "value": "DAY" },
          { "text": "Week", "value": "WEEK" },
          { "text": "Month", "value": "MONTH" },
          { "text": "Year", "value": "YEAR" }
        ],
        "optional": true
      },
      "hint": "Advanced. Leave both blank to skip exactly one cycle."
    },
    {
      "type": "input",
      "label": "Skip window length",
      "attr": "skipValue",
      "input": {"type": "text", "placeholder": "2", "optional": true},
      "hint": "Advanced. Must be filled in together with the unit."
    },
    {
      "type": "input",
      "label": "Conversation reference",
      "attr": "caller",
      "input": {"type": "text", "placeholder": "Conversation or ticket reference", "optional": true},
      "hint": "Stamped into Skio's audit trail so this change is attributable to this conversation."
    }
{{- else}}
    {
      "type": "text",
      "text": "This customer has no active Skio subscriptions to skip."
    }
{{- end}}
  ]
}
