{
{{if eq .action.result.status "success"}}
    "message": "Line item return successful.",
    "detail": "The line item with ID {{index .action.inputs.lineItemIds 0}} from Order with ID {{.action.inputs.orderId}} has been successfully returned."
{{else}}
  "errors": [
    {
      "attr": "lineItemSelection",
      "detail": "{{.action.result.message}}"
    }
  ]
{{end}}
}
