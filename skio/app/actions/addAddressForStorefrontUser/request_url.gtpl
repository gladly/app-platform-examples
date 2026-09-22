{{- /* Guards run here, not in the form - see "Merchant gates" in the app README. */ -}}

{{- if not (or (and (ne .inputs.address1 nil) (ne (.inputs.address1 | toString | trim) "")) (and (ne .inputs.address2 nil) (ne (.inputs.address2 | toString | trim) "")) (and (ne .inputs.city nil) (ne (.inputs.city | toString | trim) "")) (and (ne .inputs.company nil) (ne (.inputs.company | toString | trim) "")) (and (ne .inputs.country nil) (ne (.inputs.country | toString | trim) "")) (and (ne .inputs.doorCode nil) (ne (.inputs.doorCode | toString | trim) "")) (and (ne .inputs.firstName nil) (ne (.inputs.firstName | toString | trim) "")) (and (ne .inputs.lastName nil) (ne (.inputs.lastName | toString | trim) "")) (and (ne .inputs.province nil) (ne (.inputs.province | toString | trim) "")) (and (ne .inputs.zip nil) (ne (.inputs.zip | toString | trim) "")) (and (ne .inputs.phoneNumber nil) (ne (.inputs.phoneNumber | toString | trim) ""))) -}}
    {{- stop "Supply at least the street address and city for the new address." -}}
{{- end -}}
https://graphql.skio.com/v1/graphql
