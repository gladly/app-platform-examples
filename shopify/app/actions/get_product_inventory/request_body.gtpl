{{ if eq .inputs.ids nil }}
	{{ stop "Input ids is required."}}
{{ end }}

{{ if eq (len .inputs.ids) 0 }}
	{{ stop "Input ids must contain at least one id."}}
{{ end }}

{{ if gt (len .inputs.ids) 25 }}
	{{ stop "ids may contain at most 25 ids per call."}}
{{ end }}

{{- $query := `query GetProductInventory($ids: [ID!]!) {
    nodes(ids: $ids) {
        __typename
        ... on Product {
            id
            title
            variants(first: 50) {
                nodes {
                    id
                    title
                    sku
                    availableForSale
                    inventoryPolicy
                    inventoryQuantity
                    sellableOnlineQuantity
                    inventoryItem { tracked }
                }
                pageInfo { hasNextPage }
            }
        }
        ... on ProductVariant {
            id
            title
            sku
            availableForSale
            inventoryPolicy
            inventoryQuantity
            sellableOnlineQuantity
            inventoryItem { tracked }
            product { id title }
        }
    }
}` -}}

{
    "query": {{toJson $query}},
    "variables": {
        "ids": {{toJson .inputs.ids}}
    }
}
