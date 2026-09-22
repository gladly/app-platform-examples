{{- $selection := fromJson .lineItemSelection -}}
{
  "orderId": "{{$selection.orderId}}",
  "lineItemIds": {{toJson $selection.lineItemIds}}
}