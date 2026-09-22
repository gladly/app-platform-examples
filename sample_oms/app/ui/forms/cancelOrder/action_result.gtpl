{
{{/* Sample OMS cancelOrder action returns null for the result on success; the following logic is a workaround for that bug */}}
{{if or (eq .action.result nil)  (eq .action.result.status "success")}}
    "message": "Order cancelled successfully.",
    "detail": "Order with ID {{.action.inputs.orderId}} has been successfully cancelled."
{{else}}
  "errors": [
    {
      "attr": "orderId",
      "detail": "{{.action.result.message}}"
    }
  ]
{{end}}
}
