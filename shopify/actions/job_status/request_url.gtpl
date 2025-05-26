{{- $shop := .integration.configuration.shop}}
{{- $apiVersion := "2025-04" }}

https://{{$shop}}.myshopify.com/admin/api/{{$apiVersion}}/graphql.json