{{- /* The customer's Skio storefront-user id, straight off the profile card.

       The option value has to be the email: generateMagicLink is keyed by email, not by the
       storefront-user id, so there is no id-based fallback. That makes two different empty
       states, and they need different messages - "no Skio account" sends the agent chasing
       the wrong problem when the account exists and simply has no email on it. $hasAccount
       tells them apart. */ -}}
{{- $options := list -}}
{{- $hasAccount := false -}}
{{- $user := .data.storefront_user -}}
{{- if kindIs "map" $user -}}
    {{- $uid := $user.id | default "" | toString -}}
    {{- if ne $uid "" -}}
        {{- $hasAccount = true -}}
        {{- $who := trim (printf "%s %s" ($user.firstName | default "" | toString) ($user.lastName | default "" | toString)) -}}
        {{- if eq $who "" -}}{{- $who = $user.email | default "This customer" | toString -}}{{- end -}}
        {{- $email := $user.email | default "" | toString -}}
        {{- if ne $email "" -}}
            {{- $options = append $options (dict "text" (printf "%s - %s" $who $email) "value" $email) -}}
        {{- end -}}
    {{- end -}}
{{- end -}}
{
  "title": "Send account portal link",
{{- if gt (len $options) 0}}
  "submitButton": "Create link",
{{end -}}
  "closeButton": "Close",
  "sections": [
{{- if gt (len $options) 0}}
    {
      "type": "input",
      "label": "Customer",
      "attr": "email",
      "input": {
        "type": "select",
        "placeholder": "Please select the customer",
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
      "label": "Land on",
      "attr": "returnToPath",
      "input": {
        "type": "select",
        "placeholder": "Account home",
        "options": [
          { "text": "Subscriptions", "value": "/subscriptions" },
          { "text": "Payment methods", "value": "/payment-methods" },
          { "text": "Order history", "value": "/orders" },
          { "text": "Addresses", "value": "/addresses" }
        ],
        "optional": true
      },
      "hint": "Deep-link the customer straight to the page they need."
    }
{{- else if $hasAccount}}
    {
      "type": "text",
      "text": "This customer has a Skio account, but it has no email address on it. Skio sends the portal link to that address, so no link can be created until an email address is added to the account in Skio."
    }
{{- else}}
    {
      "type": "text",
      "text": "This customer has no Skio account, so no portal link can be created."
    }
{{- end}}
  ]
}
