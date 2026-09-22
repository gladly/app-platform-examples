{{- $input := dict "subscriptionId" (.inputs.subscriptionId | toString) "deliveryMethodType" (.inputs.deliveryMethodType | toString | trim | lower) -}}
{{- if and (ne .inputs.title nil) (ne (.inputs.title | toString | trim) "") -}}{{- $input = set $input "title" (.inputs.title | toString) -}}{{- end -}}
{{- if and (ne .inputs.presentmentTitle nil) (ne (.inputs.presentmentTitle | toString | trim) "") -}}{{- $input = set $input "presentmentTitle" (.inputs.presentmentTitle | toString) -}}{{- end -}}
{{- if and (ne .inputs.code nil) (ne (.inputs.code | toString | trim) "") -}}{{- $input = set $input "code" (.inputs.code | toString) -}}{{- end -}}
{{- if and (ne .inputs.description nil) (ne (.inputs.description | toString | trim) "") -}}{{- $input = set $input "description" (.inputs.description | toString) -}}{{- end -}}
{{- if ne .inputs.deliveryPrice nil -}}{{- $input = set $input "deliveryPrice" (.inputs.deliveryPrice | float64) -}}{{- end -}}
{{- if ne .inputs.setOverride nil -}}{{- $input = set $input "setOverride" (.inputs.setOverride) -}}{{- end -}}
{{- if and (ne .inputs.pickupLocationId nil) (ne (.inputs.pickupLocationId | toString | trim) "") -}}{{- $input = set $input "pickupLocationId" (.inputs.pickupLocationId | toString) -}}{{- end -}}
{{- if and (ne .inputs.localDeliveryInstructions nil) (ne (.inputs.localDeliveryInstructions | toString | trim) "") -}}{{- $input = set $input "localDeliveryInstructions" (.inputs.localDeliveryInstructions | toString) -}}{{- end -}}
{{- if and (ne .inputs.localDeliveryPhone nil) (ne (.inputs.localDeliveryPhone | toString | trim) "") -}}{{- $input = set $input "localDeliveryPhone" (.inputs.localDeliveryPhone | toString) -}}{{- end -}}
{
  "query": "mutation changeSubscriptionDeliveryMethod($input: ChangeSubscriptionDeliveryMethodInput!) { changeSubscriptionDeliveryMethod(input: $input) { subscriptionId } }",
  "variables": {
    "input": {{ toJson $input }}
  }
}
