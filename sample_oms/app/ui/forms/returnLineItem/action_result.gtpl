{
{{if eq .action.result.status "success"}}
    "message": "Line item return successful.",
    {{- $lineItemSelection := fromJson .formAttrs.lineItemSelection}}
    "detail": {{printf "The line item %s from order number %s has been successfully returned." $lineItemSelection.productName $lineItemSelection.orderNumber | toJson}}
{{else}}
  "errors": [
    {
      "attr": "lineItemSelection",
      "detail": "{{.action.result.message}}"
    }
  ]
{{end}}
}
