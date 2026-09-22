{{- /* Cancel or reactivate a subscription -- one form, an Operation dropdown. The subscription
       picker lists BOTH active and cancelled subscriptions (labeled with status) since cancel
       targets active and reactivate targets cancelled; the platform has no way to show/hide
       fields based on a live sibling selection, so status-labeling lets the agent self-select
       the right target instead of splitting into two forms. Reason/Send email apply only when
       cancelling -- Recharge ignores them for reactivate, and the request body (in the action)
       only sends them when operation is "cancel". Send email is a required Yes/No select (not
       optional) since there's no precedent in this app for an optional select rendering sensibly;
       an explicit choice is also clearer UX than a silently-defaulted blank. */ -}}
{{- $subs := list}}
{{- if and .data .data.subscriptions}}
  {{- range .data.subscriptions}}
    {{- $subs = append $subs .}}
  {{- end}}
{{- end}}
{
  "title": "Cancel or reactivate subscription",
{{- if $subs}}
  "submitButton": "Submit",
{{end -}}
  "closeButton": "Close",
  "sections": [
{{- if $subs}}
    {
      "type": "input",
      "label": "Operation",
      "attr": "operation",
      "input": {
        "type": "select",
        "placeholder": "Select cancel or reactivate",
        "options": [
          { "text": "Cancel subscription", "value": "cancel" },
          { "text": "Reactivate subscription", "value": "reactivate" }
        ],
        "optional": false
      }
    },
    {
      "type": "input",
      "label": "Subscription",
      "attr": "subscriptionId",
      "input": {
        "type": "select",
        "placeholder": "Select the subscription",
        "options": [
        {{- range $i, $s := $subs}}
          {{- if gt $i 0}},{{end}}
          {{- $title := default "Subscription" $s.product_title}}
          {{- $status := $s.status | toString | title}}
          {{- $label := printf "%s — #%s — %s" $title $s.id $status}}
          {
            "text": {{ $label | toJson }},
            "value": {{ $s.id | toJson }}
          }
        {{- end}}
        ],
        "optional": false
      }
    },
    {
      "type": "input",
      "label": "Reason",
      "attr": "reason",
      "input": { "type": "text", "placeholder": "Customer requested cancellation", "optional": true },
      "hint": "Used only when cancelling. Ignored when reactivating."
    },
    {
      "type": "input",
      "label": "Send email",
      "attr": "sendEmail",
      "input": {
        "type": "select",
        "placeholder": "Notify the customer by email",
        "options": [
          { "text": "Yes", "value": "true" },
          { "text": "No", "value": "false" }
        ],
        "optional": false
      },
      "hint": "Used only when cancelling. Ignored when reactivating."
    }
{{- else}}
    {
      "type": "text",
      "text": "This customer has no Recharge subscriptions to cancel or reactivate."
    }
{{- end}}
  ]
}
