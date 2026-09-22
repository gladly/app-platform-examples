{{- $input := dict "subscriptionId" (.inputs.subscriptionId | toString) -}}
{{- /* Two mutually exclusive modes. Pointing at an address already on file is what V1 of this
       mutation cannot do, and it is the common case: the customer says "use my work address". */ -}}
{{- if and (ne .inputs.newShippingAddressPlatformId nil) (ne (.inputs.newShippingAddressPlatformId | toString | trim) "") -}}
    {{- $input = set $input "newShippingAddressPlatformId" (.inputs.newShippingAddressPlatformId | toString | trim) -}}
{{- else -}}
    {{- $addr := dict -}}
    {{- if and (ne .inputs.address1 nil) (ne (.inputs.address1 | toString | trim) "") -}}{{- $addr = set $addr "address1" (.inputs.address1 | toString) -}}{{- end -}}
    {{- if and (ne .inputs.address2 nil) (ne (.inputs.address2 | toString | trim) "") -}}{{- $addr = set $addr "address2" (.inputs.address2 | toString) -}}{{- end -}}
    {{- if and (ne .inputs.city nil) (ne (.inputs.city | toString | trim) "") -}}{{- $addr = set $addr "city" (.inputs.city | toString) -}}{{- end -}}
    {{- if and (ne .inputs.company nil) (ne (.inputs.company | toString | trim) "") -}}{{- $addr = set $addr "company" (.inputs.company | toString) -}}{{- end -}}
    {{- if and (ne .inputs.country nil) (ne (.inputs.country | toString | trim) "") -}}{{- $addr = set $addr "country" (.inputs.country | toString) -}}{{- end -}}
    {{- if and (ne .inputs.doorCode nil) (ne (.inputs.doorCode | toString | trim) "") -}}{{- $addr = set $addr "doorCode" (.inputs.doorCode | toString) -}}{{- end -}}
    {{- if and (ne .inputs.firstName nil) (ne (.inputs.firstName | toString | trim) "") -}}{{- $addr = set $addr "firstName" (.inputs.firstName | toString) -}}{{- end -}}
    {{- if and (ne .inputs.lastName nil) (ne (.inputs.lastName | toString | trim) "") -}}{{- $addr = set $addr "lastName" (.inputs.lastName | toString) -}}{{- end -}}
    {{- if and (ne .inputs.province nil) (ne (.inputs.province | toString | trim) "") -}}{{- $addr = set $addr "province" (.inputs.province | toString) -}}{{- end -}}
    {{- if and (ne .inputs.zip nil) (ne (.inputs.zip | toString | trim) "") -}}{{- $addr = set $addr "zip" (.inputs.zip | toString) -}}{{- end -}}
    {{- if and (ne .inputs.phone nil) (ne (.inputs.phone | toString | trim) "") -}}{{- $addr = set $addr "phone" (.inputs.phone | toString) -}}{{- end -}}
    {{- $input = set $input "updatedShippingAddress" $addr -}}
{{- end -}}
{
  "query": "mutation updateSubscriptionShippingAddressV2($input: UpdateSubscriptionShippingAddressV2Input!) { updateSubscriptionShippingAddressV2(input: $input) { ok message } }",
  "variables": {
    "input": {{ toJson $input }}
  }
}
