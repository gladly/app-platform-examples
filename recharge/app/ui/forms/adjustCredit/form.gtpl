{{- /* The credit balance is shown ONLY inside this agent-invoked form (KTD6); it is
       never placed on the auto-loaded profile card. Inputs map 1:1 to adjustCredit, so there is no
       action_inputs.gtpl. The amount cap and the type-approve confirmation are enforced by the
       action itself (the form cannot read integration.configuration). */ -}}
{{- $accounts := list}}
{{- if and .data .data.credit_accounts}}
  {{- range .data.credit_accounts}}{{- $accounts = append $accounts .}}{{- end}}
{{- end}}
{
  "title": "Adjust store credit",
{{- if $accounts}}
  "submitButton": "Adjust credit",
{{end -}}
  "closeButton": "Close",
  "sections": [
{{- if $accounts}}
    {{- $balanceText := "Current credit balance:"}}
    {{- range $accounts}}
      {{- $balanceText = printf "%s\n%s %s (%s)" $balanceText (default "0.00" .available_balance) (default "" .currency_code) (default "Credit account" .name)}}
    {{- end}}
    {
      "type": "text",
      "text": {{ $balanceText | toJson }}
    },
    {
      "type": "input",
      "label": "Credit account",
      "attr": "creditAccountId",
      "input": {
        "type": "select",
        "placeholder": "Select the credit account",
        "options": [
        {{- range $i, $a := $accounts}}
          {{- if gt $i 0}},{{end}}
          {
            "text": {{ printf "%s (balance %s %s)" (default "Credit account" $a.name) (default "0.00" $a.available_balance) (default "" $a.currency_code) | toJson }},
            "value": {{ $a.id | toJson }}
          }
        {{- end}}
        ],
        "optional": false
      }
    },
    {
      "type": "input",
      "label": "Amount",
      "attr": "amount",
      "input": { "type": "text", "placeholder": "e.g. 10.00", "optional": false },
      "hint": "Decimal amount. Larger than the merchant's configured maximum is rejected."
    },
    {
      "type": "input",
      "label": "Direction",
      "attr": "type",
      "input": {
        "type": "select",
        "placeholder": "Credit or debit",
        "options": [
          { "text": "Credit (add to balance)", "value": "credit" },
          { "text": "Debit (remove from balance)", "value": "debit" }
        ],
        "optional": false
      }
    },
    {
      "type": "input",
      "label": "Note",
      "attr": "note",
      "input": { "type": "text", "placeholder": "Reason for the adjustment", "optional": true }
    },
    {
      "type": "input",
      "label": "Confirmation",
      "attr": "confirmationCopy",
      "input": { "type": "text", "placeholder": "approve", "optional": false },
      "hint": "Type approve to confirm. Only adjust an amount the customer actually requested, after verifying their identity."
    }
{{- else}}
    {
      "type": "text",
      "text": "This customer has no Recharge credit account."
    }
{{- end}}
  ]
}
