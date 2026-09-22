{{- $shop := .integration.configuration.shop}}
{{- $apiVersion := "2026-07" }}

https://{{$shop}}.myshopify.com/admin/api/{{$apiVersion}}/graphql.json