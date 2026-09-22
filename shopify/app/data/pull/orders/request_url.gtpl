{{- $shop := .integration.configuration.shop}}
{{- $apiVersion := "2025-04"}}

{{/* Shopify GRAPHQL url */}}

https://{{$shop}}.myshopify.com/admin/api/{{$apiVersion}}/graphql.json