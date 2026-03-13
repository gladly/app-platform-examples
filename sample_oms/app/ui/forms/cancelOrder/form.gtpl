{{$cancellableOrders := list}}
{{if and .data .data.orders}}
  {{range .data.orders}}
    {{if eq .status "pending"}}
      {{$cancellableOrders = append $cancellableOrders .}}
    {{end}}
  {{end}}
{{end}}
{
  "title": "Cancel order",
{{if $cancellableOrders}}
  "submitButton": "Cancel Order",
{{end}}
  "closeButton": "Close",
  "sections": [
{{if $cancellableOrders}}
    {
        "type": "input",
        "label": "Order",
        "attr": "orderId",
        "input": {
            "type": "select",
            "placeholder": "Please select an Order",
            "options": [
            {{range $i, $order := $cancellableOrders}}
                {{if gt $i 0}},{{end}}
				{
                    "text": "Order #{{$order.orderNumber}} - {{range $j, $item := $order.lineItems}}{{if gt $j 0}}, {{end}}{{$item.product.name}} ({{$item.quantity}}){{end}}",
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
        "placeholder": "Please select a reason for the cancellation",
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
            }
        ],
        "optional": false
      }
    }
{{else}}
    {
        "type": "text",
        "text": "The customer does not have any orders that can be cancelled."
    }
{{end}}
  ]
}
