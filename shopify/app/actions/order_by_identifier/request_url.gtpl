{{- $shop := .integration.configuration.shop}}
{{- $apiVersion := "2026-07"}}

{{/* Deep-dive order read by GID (orderByIdentifier). */}}

https://{{$shop}}.myshopify.com/admin/api/{{$apiVersion}}/graphql.json