{
{{- if .action.result.error}}
  "errors": [
    {
      "attr": "addressId",
      "detail": {{ .action.result.error.error_message | toJson }}
    }
  ]
{{- else}}
  {{- $a := .action.result.address}}
  "message": "Address updated.",
  "detail": {{ printf "The shipping address is now %s, %s %s %s %s." $a.address1 $a.city $a.province $a.zip $a.country_code | toJson }}
{{- end}}
}
