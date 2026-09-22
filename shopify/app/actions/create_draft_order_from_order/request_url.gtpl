{{- $shop := .integration.configuration.shop}}
{{- $apiVersion := "2026-04"}}

{{/* Create a draft order from an existing order */}}

https://{{$shop}}.myshopify.com/admin/api/{{$apiVersion}}/graphql.json
