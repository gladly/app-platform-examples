{{- /* Recharge wants external_variant_id as an ExternalId object {"ecommerce": <id>}, not a bare
       scalar (a bare int/string 422s "Invalid input type"). Verified live 2026-07-01. */ -}}
{{- $body := dict "external_variant_id" (dict "ecommerce" .inputs.externalVariantId) -}}
{{- if and (ne .inputs.quantity nil) (ne (toString .inputs.quantity) "") -}}{{- $body = set $body "quantity" (.inputs.quantity | int64) -}}{{- end -}}
{{- if and (ne .inputs.price nil) (ne (toString .inputs.price) "") -}}{{- $body = set $body "price" (toString .inputs.price) -}}{{- end -}}

{{ toJson $body }}
