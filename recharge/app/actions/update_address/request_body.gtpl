{{- /* Send only the fields the agent actually provided (non-empty). camelCase inputs map to
       Recharge's snake_case body keys. When countryCode changes, the form is expected to also
       provide a zip valid for the new country (Recharge 422s on the zip otherwise). */ -}}
{{- $body := dict -}}
{{- if and (ne .inputs.address1 nil) (ne .inputs.address1 "") -}}{{- $body = set $body "address1" .inputs.address1 -}}{{- end -}}
{{- if and (ne .inputs.address2 nil) (ne .inputs.address2 "") -}}{{- $body = set $body "address2" .inputs.address2 -}}{{- end -}}
{{- if and (ne .inputs.city nil) (ne .inputs.city "") -}}{{- $body = set $body "city" .inputs.city -}}{{- end -}}
{{- if and (ne .inputs.province nil) (ne .inputs.province "") -}}{{- $body = set $body "province" .inputs.province -}}{{- end -}}
{{- if and (ne .inputs.zip nil) (ne .inputs.zip "") -}}{{- $body = set $body "zip" .inputs.zip -}}{{- end -}}
{{- if and (ne .inputs.countryCode nil) (ne .inputs.countryCode "") -}}{{- $body = set $body "country_code" .inputs.countryCode -}}{{- end -}}
{{- if and (ne .inputs.firstName nil) (ne .inputs.firstName "") -}}{{- $body = set $body "first_name" .inputs.firstName -}}{{- end -}}
{{- if and (ne .inputs.lastName nil) (ne .inputs.lastName "") -}}{{- $body = set $body "last_name" .inputs.lastName -}}{{- end -}}
{{- if and (ne .inputs.phone nil) (ne .inputs.phone "") -}}{{- $body = set $body "phone" .inputs.phone -}}{{- end -}}
{{- if and (ne .inputs.company nil) (ne .inputs.company "") -}}{{- $body = set $body "company" .inputs.company -}}{{- end -}}
{{ toJson $body }}
