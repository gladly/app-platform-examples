{{- $shop := .integration.configuration.shop}}
{{- $apiVersion := "2025-01"}}

{{/* Look up order by num */}}

https://{{$shop}}.myshopify.com/admin/api/{{$apiVersion}}/graphql.json