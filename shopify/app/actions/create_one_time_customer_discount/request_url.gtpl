{{- $shop := .integration.configuration.shop}}
{{- $apiVersion := "2026-04" }}

{{/* Create a one-time, single-customer basic code discount */}}

https://{{$shop}}.myshopify.com/admin/api/{{$apiVersion}}/graphql.json
