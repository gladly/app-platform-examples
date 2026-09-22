{
{{if eq .action.result.status "success"}}
    "message": "Order refund successful.",
    "detail": "Order with ID {{.action.inputs.orderId}} has been successfully refunded."
{{else}}
  "errors": [
    {
      "attr": "orderId",
      "detail": "{{.action.result.message}}"
    }
  ]
{{end}}
}