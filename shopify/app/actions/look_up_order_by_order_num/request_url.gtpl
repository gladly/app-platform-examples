{{- $shop := .integration.configuration.shop}}
{{- $apiVersion := "2026-04"}}

{{/* Look up order by num */}}

https://{{$shop}}.myshopify.com/admin/api/{{$apiVersion}}/graphql.json