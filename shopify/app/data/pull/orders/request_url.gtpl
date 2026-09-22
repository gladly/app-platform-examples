{{- $shop := .integration.configuration.shop}}
{{- $apiVersion := "2026-07"}}

{{/* Shopify GRAPHQL url */}}

https://{{$shop}}.myshopify.com/admin/api/{{$apiVersion}}/graphql.json