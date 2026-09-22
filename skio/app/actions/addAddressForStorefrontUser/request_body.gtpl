{{- $input := dict "storefrontUserId" (.inputs.storefrontUserId | toString) -}}
{{- if and (ne .inputs.address1 nil) (ne (.inputs.address1 | toString | trim) "") -}}{{- $input = set $input "address1" (.inputs.address1 | toString) -}}{{- end -}}
{{- if and (ne .inputs.address2 nil) (ne (.inputs.address2 | toString | trim) "") -}}{{- $input = set $input "address2" (.inputs.address2 | toString) -}}{{- end -}}
{{- if and (ne .inputs.city nil) (ne (.inputs.city | toString | trim) "") -}}{{- $input = set $input "city" (.inputs.city | toString) -}}{{- end -}}
{{- if and (ne .inputs.company nil) (ne (.inputs.company | toString | trim) "") -}}{{- $input = set $input "company" (.inputs.company | toString) -}}{{- end -}}
{{- if and (ne .inputs.country nil) (ne (.inputs.country | toString | trim) "") -}}{{- $input = set $input "country" (.inputs.country | toString) -}}{{- end -}}
{{- if and (ne .inputs.doorCode nil) (ne (.inputs.doorCode | toString | trim) "") -}}{{- $input = set $input "doorCode" (.inputs.doorCode | toString) -}}{{- end -}}
{{- if and (ne .inputs.firstName nil) (ne (.inputs.firstName | toString | trim) "") -}}{{- $input = set $input "firstName" (.inputs.firstName | toString) -}}{{- end -}}
{{- if and (ne .inputs.lastName nil) (ne (.inputs.lastName | toString | trim) "") -}}{{- $input = set $input "lastName" (.inputs.lastName | toString) -}}{{- end -}}
{{- if and (ne .inputs.province nil) (ne (.inputs.province | toString | trim) "") -}}{{- $input = set $input "province" (.inputs.province | toString) -}}{{- end -}}
{{- if and (ne .inputs.zip nil) (ne (.inputs.zip | toString | trim) "") -}}{{- $input = set $input "zip" (.inputs.zip | toString) -}}{{- end -}}
{{- if and (ne .inputs.phoneNumber nil) (ne (.inputs.phoneNumber | toString | trim) "") -}}{{- $input = set $input "phoneNumber" (.inputs.phoneNumber | toString) -}}{{- end -}}
{
  "query": "mutation addAddressForStorefrontUser($input: CreateAddressForStorefrontUserInput!) { addAddressForStorefrontUser(input: $input) { id platformId } }",
  "variables": {
    "input": {{ toJson $input }}
  }
}
