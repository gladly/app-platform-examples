{{$refundableOrders := list}}
{{if and .data .data.orders}}
  {{range .data.orders}}
    {{if or (eq .status "delivered") (eq .status "shipping")}}
      {{$refundableOrders = append $refundableOrders .}}
    {{end}}
  {{end}}
{{end}}
{
  "title": "Refund order",
{{if $refundableOrders}}
  "submitButton": "Refund Order",
{{end}}
  "closeButton": "Close",
  "sections": [
{{if $refundableOrders}}
    {
        "type": "input",
        "label": "Order",
        "attr": "orderId",
        "input": {
            "type": "select",
            "placeholder": "Please select an Order",
            "options": [
            {{range $i, $order := $refundableOrders}}
                {{if gt $i 0}},{{end}}
				{
                    "text": "Order #{{$order.orderNumber}} ({{$order.status}}) - {{range $j, $item := $order.lineItems}}{{if gt $j 0}}, {{end}}{{$item.product.name}} ({{$item.quantity}}){{end}}",
                    "value": "{{.id}}"
                }
            {{end}}
            ],
            "optional": false
        }
    },
    {
      "type": "input",
      "attr": "reason",
	    "label": "Reason",
      "input": {
        "type": "select",
        "placeholder": "Please select a reason for the refund",
        "options": [
            {
              "text": "Customer Request",
              "value": "customer_request"
            },
            {
              "text": "Out of Stock",
              "value": "out_of_stock"
            },
            {
              "text": "Broken",
              "value": "broken"
            },
            {
              "text": "Not Delivered",
              "value": "not_delivered"
            },
            {
              "text": "Late Delivery",
              "value": "late_delivery"
            },
            {
              "text": "Compensation",
              "value": "compensation"
            }
        ],
        "optional": false
      }
    }
{{else}}
    {
        "type": "text",
        "text": "The customer does not have any orders that can be refunded."
    }
{{end}}
  ]
}
