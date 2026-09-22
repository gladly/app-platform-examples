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
{{- /* Addresses already on the customer's Skio account. The value is the
       platform id, which is what updateSubscriptionShippingAddressV2 points a subscription at -
       something V1 of that mutation cannot do at all. */ -}}
{{- $options2 := list -}}
{{- $user := .data.storefront_user -}}
{{- if kindIs "map" $user -}}
    {{- range $a := (default list $user.ShippingAddresses) -}}
        {{- $pid := $a.platformId | default "" | toString -}}
        {{- if ne $pid "" -}}
            {{- $who := trim (printf "%s %s" ($a.firstName | default "" | toString) ($a.lastName | default "" | toString)) -}}
            {{- $where := $a.address1 | default "" | toString -}}
            {{- if and (ne $a.city nil) (ne ($a.city | toString) "") -}}
                {{- $where = printf "%s, %s" $where ($a.city | toString) -}}
            {{- end -}}
            {{- if and (ne $a.province nil) (ne ($a.province | toString) "") -}}
                {{- $where = printf "%s %s" $where ($a.province | toString) -}}
            {{- end -}}
            {{- if and (ne $a.zip nil) (ne ($a.zip | toString) "") -}}
                {{- $where = printf "%s %s" $where ($a.zip | toString) -}}
            {{- end -}}
            {{- $label := $where -}}
            {{- if ne $who "" -}}{{- $label = printf "%s - %s" $who $where -}}{{- end -}}
            {{- $options2 = append $options2 (dict "text" $label "value" $pid) -}}
        {{- end -}}
    {{- end -}}
{{- end -}}
{
  "title": "Change shipping address",
{{- if gt (len $options) 0}}
  "submitButton": "Change address",
{{end -}}
  "closeButton": "Close",
  "sections": [
{{- if gt (len $options) 0}}
    {
      "type": "text",
      "text": "Either pick an address the customer already has on file, or type a new one. If you type a new one, fill in every field that should be set - the whole address is replaced."
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
    {{- /* A select cannot be un-picked in the desktop, so the "none" option is the only way back
           to the typed fields. Its value is the sentinel "__none__", not "": an option `value` is
           documented as falling back to the option `text` when it is not provided, and an empty
           string is indistinguishable from absent under a falsy check - that fallback would submit
           the label itself as a platform id. action_inputs.gtpl drops the sentinel. */ -}}
    {
      "type": "input",
      "label": "Use an address already on file",
      "attr": "newShippingAddressPlatformId",
      "input": {
        "type": "select",
        "placeholder": "Or type a new address below",
        "options": [
          { "text": "None - type a new address below", "value": "__none__" }
        {{- range $o := $options2}},
          { "text": {{ $o.text | toJson }}, "value": {{ $o.value | toJson }} }
        {{- end}}
        ],
        "optional": true
      },
      "hint": "Pick one of the customer's saved addresses and leave the fields below blank. Pick \"None\" to go back to typing a new address."
    },
    {
      "type": "input",
      "label": "First name",
      "attr": "firstName",
      "input": {"type": "text", "placeholder": "Ada", "optional": true}
    },
    {
      "type": "input",
      "label": "Last name",
      "attr": "lastName",
      "input": {"type": "text", "placeholder": "Lovelace", "optional": true}
    },
    {
      "type": "input",
      "label": "Company",
      "attr": "company",
      "input": {"type": "text", "placeholder": "Analytical Engines", "optional": true}
    },
    {
      "type": "input",
      "label": "Address line 1",
      "attr": "address1",
      "input": {"type": "text", "placeholder": "500 W 2nd St", "optional": true}
    },
    {
      "type": "input",
      "label": "Address line 2",
      "attr": "address2",
      "input": {"type": "text", "placeholder": "Unit 4", "optional": true}
    },
    {
      "type": "input",
      "label": "City",
      "attr": "city",
      "input": {"type": "text", "placeholder": "Austin", "optional": true}
    },
    {
      "type": "input",
      "label": "State / Province",
      "attr": "province",
      "input": {"type": "text", "placeholder": "TX", "optional": true}
    },
    {
      "type": "input",
      "label": "Country",
      "attr": "country",
      "input": {"type": "text", "placeholder": "United States", "optional": true}
    },
    {
      "type": "input",
      "label": "ZIP / Postal code",
      "attr": "zip",
      "input": {"type": "text", "placeholder": "78701", "optional": true}
    },
    {
      "type": "input",
      "label": "Door code",
      "attr": "doorCode",
      "input": {"type": "text", "placeholder": "1984", "optional": true}
    },
    {
      "type": "input",
      "label": "Phone",
      "attr": "phone",
      "input": {"type": "text", "placeholder": "+15125550123", "optional": true}
    }
{{- else}}
    {
      "type": "text",
      "text": "This customer has no active Skio subscriptions."
    }
{{- end}}
  ]
}
