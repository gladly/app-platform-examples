{{- /* Metafields are retrieved for customer and their orders */ -}}
{{- $ordersLimit := 40 -}}
{{- with index .integration.configuration "ordersLimit" -}}
{{- $parsed := atoi (printf "%v" .) -}}
{{- if le $parsed 0 }}{{ fail (printf "Shopify 'ordersLimit' must be a positive whole number, got %v. Metafields for this customer's orders cannot be retrieved." .) }}{{ end -}}
{{- $ordersLimit = $parsed -}}
{{- end -}}
{{- /* metafieldsLimit is undocumented and deliberately has no admin form field;
    it is slated for removal in v6, so it is left unvalidated. */ -}}
{{- $metafieldsLimit := (default 50 (index .integration.configuration "metafieldsLimit")) -}}

{{- range .externalData.shopify_customer -}}
{{- $customerId := .id -}}

{{ $query := printf `query {
    customer(id: "%s") {
        id
        metafields(first: %v) {
            nodes {
                id
                namespace
                key
                value
                type
                createdAt
                ownerType
            }
        }
        orders(first: %v, reverse: true, sortKey: UPDATED_AT) {
            nodes {
                id
                metafields(first: %v) {
                    nodes {
                        id
                        namespace
                        key
                        value
                        type
                        createdAt
                        ownerType
                    }
                }
            }
        }
    }
}
` $customerId $metafieldsLimit $ordersLimit $metafieldsLimit }}
#body
{
  "query": {{toJson $query}}
}
{{ end -}}
