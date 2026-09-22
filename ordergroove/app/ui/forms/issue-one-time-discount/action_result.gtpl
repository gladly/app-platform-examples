{{- /* Every refusal - toggle off, cap unset, cap exceeded, missing approval - reaches
       here as an absent result, because the action stops before the request is built. */ -}}
{{- if not .action.result -}}
{
  "errors": [
    {
      "attr": "confirmed",
      "detail": "The discount was not issued. The reason is shown in the conversation timeline - most often the store's discount limit, or discounts being turned off for this store."
    }
  ]
}
{{- else -}}
{
  "message": "Discount issued",
  "detail": {{ printf "%s %s was applied. It takes effect when the order places - refresh the card to confirm the new total." (printf "%v" .action.inputs.discountType) (printf "%v" .action.inputs.value) | toJson }}
}
{{- end -}}
