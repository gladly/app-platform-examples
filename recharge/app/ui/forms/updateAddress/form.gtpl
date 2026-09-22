{{- /* Pick which address to update, then fill in only the fields to change. Every text input is
       optional and blank; the updateAddress action sends only non-empty fields, so blanks never
       clobber existing values. Attr names match the action's GraphQL inputs 1:1, so no
       action_inputs.gtpl is needed. */ -}}
{{- $addrs := list}}
{{- if and .data .data.addresses}}
  {{- range .data.addresses}}
    {{- $addrs = append $addrs .}}
  {{- end}}
{{- end}}
{
  "title": "Update shipping address",
{{- if $addrs}}
  "submitButton": "Update address",
{{end -}}
  "closeButton": "Close",
  "sections": [
{{- if $addrs}}
    {
      "type": "input",
      "label": "Address",
      "attr": "addressId",
      "input": {
        "type": "select",
        "placeholder": "Select the address to update",
        "options": [
        {{- range $i, $a := $addrs}}
          {{- if gt $i 0}},{{end}}
          {{- $label := printf "%s, %s %s %s %s" $a.address1 $a.city $a.province $a.zip $a.country_code}}
          {
            "text": {{ $label | toJson }},
            "value": {{ $a.id | toJson }}
          }
        {{- end}}
        ],
        "optional": false
      }
    },
    {
      "type": "text",
      "text": "Fill in only the fields you want to change. Leave the rest blank to keep their current values."
    },
    { "type": "input", "label": "Address line 1", "attr": "address1", "input": { "type": "text", "placeholder": "New address line 1", "optional": true } },
    { "type": "input", "label": "Address line 2", "attr": "address2", "input": { "type": "text", "placeholder": "New address line 2", "optional": true } },
    { "type": "input", "label": "City", "attr": "city", "input": { "type": "text", "placeholder": "New city", "optional": true } },
    { "type": "input", "label": "State / Province", "attr": "province", "input": { "type": "text", "placeholder": "New state/province", "optional": true } },
    { "type": "input", "label": "ZIP / Postal code", "attr": "zip", "input": { "type": "text", "placeholder": "New ZIP/postal code", "optional": true } },
    { "type": "input", "label": "Country code", "attr": "countryCode", "input": { "type": "text", "placeholder": "e.g. US, CA", "optional": true }, "hint": "If you change the country, you MUST also set a ZIP valid for the new country, or the update is rejected." },
    { "type": "input", "label": "First name", "attr": "firstName", "input": { "type": "text", "placeholder": "New first name", "optional": true } },
    { "type": "input", "label": "Last name", "attr": "lastName", "input": { "type": "text", "placeholder": "New last name", "optional": true } },
    { "type": "input", "label": "Phone", "attr": "phone", "input": { "type": "text", "placeholder": "New phone", "optional": true } },
    { "type": "input", "label": "Company", "attr": "company", "input": { "type": "text", "placeholder": "New company", "optional": true } }
{{- else}}
    {
      "type": "text",
      "text": "This customer has no addresses on file."
    }
{{- end}}
  ]
}
