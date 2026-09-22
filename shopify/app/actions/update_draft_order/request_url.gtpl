{{- $shop := .integration.configuration.shop}}
{{- $apiVersion := "2026-04"}}

{{/* Update basic fields on an existing draft order */}}

https://{{$shop}}.myshopify.com/admin/api/{{$apiVersion}}/graphql.json
