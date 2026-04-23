{{$returnableLineItems := list}}
{{if and .data .data.orders}}
  {{range .data.orders}}
    {{$order := .}}
    {{range .lineItems}}
      {{if and (eq .status "delivered") $order.id .id}}
        {{$returnableLineItems = append $returnableLineItems (dict "order" $order "lineItem" .)}}
      {{end}}
    {{end}}
  {{end}}
{{end}}
{
  "title": "Return line item",
{{if $returnableLineItems}}
  "submitButton": "Return Line Item",
{{end}}
  "closeButton": "Close",
  "sections": [
{{if $returnableLineItems}}
    {
        "type": "input",
        "label": "Line Item",
        "attr": "lineItemSelection",
        "input": {
            "type": "select",
            "placeholder": "Please select a line item to return",
            "options": [
            {{range $i, $item := $returnableLineItems}}
                {{if gt $i 0}},{{end}}
                {
                    "text": "Order #{{$item.order.orderNumber}} {{$item.lineItem.product.name}}",
                    {{$value := dict
                      "orderId" $item.order.id
                      "lineItemIds" (list $item.lineItem.id)
                    }}
                    "value": "{{toJson $value | b64enc}}"
                }
            {{end}}
            ],
            "optional": false
        }
    }
{{else}}
    {
        "type": "text",
        "text": "The customer does not have any line items that can be returned."
    }
{{end}}
  ]
}
