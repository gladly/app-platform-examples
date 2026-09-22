{{- /* Guards run here, not in the form - see "Merchant gates" in the app README. */ -}}

{{- /* Allowlist from merchant config, fail closed when unset. The merchant decides which codes
       an agent may hand out; a model cannot emit a merchant's config value. */ -}}
{{- $code := .inputs.code | default "" | toString | trim -}}
{{- /* Read via `index` (chargebee/yotpo idiom) so the admin form field can stay optional -- a
       direct selector read would make appcfg require it. Missing or blank still fails closed. */ -}}
{{- $allowedRaw := index .integration.configuration "allowedDiscountCodes" | default "" | toString -}}
{{- if eq (trim $allowedRaw) "" -}}
    {{- stop "No discount codes are allowed for this app. An admin must list them in \"Discount codes agents may apply\" in the Skio app configuration before agents can apply one." -}}
{{- end -}}
{{- $allowed := list -}}
{{- range $entry := splitList "," $allowedRaw -}}
    {{- $entry = trim $entry | upper -}}
    {{- if ne $entry "" -}}{{- $allowed = append $allowed $entry -}}{{- end -}}
{{- end -}}
{{- if not (has (upper $code) $allowed) -}}
    {{- stop (printf "%q is not in the merchant's allowed discount codes (%s). A manager must apply anything else directly in Skio." $code (join ", " $allowed)) -}}
{{- end -}}

{{- /* Confirmation checkbox: human-path friction, NOT the access control. */ -}}
{{- if ne (.inputs.confirmed | default false) true -}}
    {{- stop "Check the confirmation box to apply this discount." -}}
{{- end -}}
https://graphql.skio.com/v1/graphql
