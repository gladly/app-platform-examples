{{ if eq .inputs.ownerIds nil }}
	{{ stop "Input ownerIds is required."}}
{{ end }}

{{ if gt (len .inputs.ownerIds) 25 }}
	{{ stop "ownerIds may contain at most 25 ids per call."}}
{{ end }}

{{- $hasKeys := and (ne .inputs.keys nil) (gt (len .inputs.keys) 0) -}}

{{- $graphqlQuery := "" -}}
{{- if $hasKeys -}}
    {{- $graphqlQuery = `query GetMetafieldsByOwnerId($ownerIds: [ID!]!, $keys: [String!]!) {
    nodes(ids: $ownerIds) {
        __typename
        id
        ... on HasMetafields {
            metafields(first: 50, keys: $keys) {
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
}` -}}
{{- else -}}
    {{- $graphqlQuery = `query GetMetafieldsByOwnerId($ownerIds: [ID!]!) {
    nodes(ids: $ownerIds) {
        __typename
        id
        ... on HasMetafields {
            metafields(first: 50) {
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
}` -}}
{{- end -}}

{
    "query": {{toJson $graphqlQuery}},
    "variables": {
        "ownerIds": {{toJson .inputs.ownerIds}}{{ if $hasKeys }},
        "keys": {{toJson .inputs.keys}}{{ end }}
    }
}
