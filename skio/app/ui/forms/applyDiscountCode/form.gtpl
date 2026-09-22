{{- /* One option per eligible subscription. The label leads with the product the
       customer would name, not the uuid - the uuid is only ever the value. */ -}}
{{- $options := list -}}
{{- range $s := (default list .data.subscriptions) -}}
    {{- $status := $s.status | default "" | toString | upper -}}
    {{- $context := $s.statusContext | default "" | toString | upper -}}
    {{- if ne $status "CANCELLED" -}}
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
  "title": "Apply discount code",
{{- if gt (len $options) 0}}
  "submitButton": "Apply discount",
{{end -}}
  "closeButton": "Close",
  "sections": [
{{- if gt (len $options) 0}}
    {
      "type": "text",
      "text": "Only discount codes your admin has allowed can be applied. Anything else is refused and has to be applied in Skio by a manager."
    },
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
      "label": "Discount code",
      "attr": "code",
      "input": {"type": "text", "placeholder": "WELCOME10", "optional": false},
      "hint": "Must be one of the codes your admin has allowed for agents."
    },
    {
      "type": "input",
      "label": "Confirmation",
      "attr": "confirmed",
      "input": {
        "type": "checkbox",
        "text": "I confirm the customer asked for this discount and I have verified their identity"
      },
      "hint": "The checkbox starts unchecked and must be selected for every discount request."
    }
{{- else}}
    {
      "type": "text",
      "text": "This customer has no active Skio subscriptions to discount."
    }
{{- end}}
  ]
}
