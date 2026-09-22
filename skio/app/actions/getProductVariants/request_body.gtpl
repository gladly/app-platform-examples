{{- /* Substring search on variant title or SKU. Skio caps a response at 100 nodes, so the
       limit is clamped to 50 - an unbounded list query is a live node-limit failure. */ -}}
{{- $search := .inputs.search | default "" | toString | trim -}}
{{- if eq $search "" -}}
    {{- stop "Enter part of a product title or SKU to search for." -}}
{{- end -}}
{{- $limit := 25 -}}
{{- if ne .inputs.limit nil -}}{{- $limit = .inputs.limit | int -}}{{- end -}}
{{- if lt $limit 1 -}}{{- $limit = 1 -}}{{- end -}}
{{- if gt $limit 50 -}}{{- $limit = 50 -}}{{- end -}}
{
  "query": "query getProductVariants($search: String!, $limit: Int!) { ProductVariants(where: {_or: [{title: {_ilike: $search}}, {sku: {_ilike: $search}}]}, limit: $limit, order_by: {title: asc}) { id title sku platformId price compareAtPrice outOfStockAt Product { title } } }",
  "variables": {
    "search": {{ printf "%%%s%%" $search | toJson }},
    "limit": {{ $limit }}
  }
}
