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
  "title": "Cancel subscription",
{{- if gt (len $options) 0}}
  "submitButton": "Cancel subscription",
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
      "label": "Customer notification",
      "attr": "shouldSendNotif",
      "input": { "type": "checkbox", "text": "Send Skio's cancellation email or SMS" }
    },
    {
      "type": "input",
      "label": "Win-back journeys",
      "attr": "invokeSubscriptionCancelJourneys",
      "input": { "type": "checkbox", "text": "Run the merchant's Skio cancellation journeys" }
    },
    {
      "type": "input",
      "label": "Cancel-flow session",
      "attr": "cancelSessionId",
      "input": {"type": "text", "placeholder": "Cancel-flow session id", "optional": true},
      "hint": "Fill this in when the customer already went through Skio's cancel flow, so Skio's retention reporting attributes this cancellation to it."
    },
    {
      "type": "input",
      "label": "Permanent",
      "attr": "permanentlyCancel",
      "input": { "type": "checkbox", "text": "Permanently cancel - this can NEVER be reactivated" }
    },
    {{- /* The three checkboxes above choose what the cancellation DOES. This last one gates the
           irreversible option only: the action reads `confirmed` inside the permanentlyCancel
           branch and nowhere else, so an ordinary reversible cancellation never needs it. Text
           and hint have to say that, or an agent reads a requirement the action does not have. */}}
    {
      "type": "input",
      "label": "Confirmation",
      "attr": "confirmed",
      "input": { "type": "checkbox", "text": "I confirm this subscription should be permanently cancelled and can never be reactivated." },
      "hint": "Only needed when Permanent is checked above - that cancellation is refused without it. An ordinary cancellation is reversible and needs no confirmation."
    }
{{- else}}
    {
      "type": "text",
      "text": "This customer has no Skio subscriptions that can be cancelled."
    }
{{- end}}
  ]
}
