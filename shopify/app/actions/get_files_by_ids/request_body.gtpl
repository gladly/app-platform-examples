{{ if eq .inputs.ids nil }}
	{{ stop "Input ids is required."}}
{{ end }}

{{ if gt (len .inputs.ids) 25 }}
	{{ stop "ids may contain at most 25 ids per call."}}
{{ end }}

{{- /*
    File fields are gated individually rather than per object, and Shopify
    rejects the whole operation when any one selected field is denied.

    `fileErrors` (on File) and `mediaErrors` / `mediaWarnings` (on MediaImage)
    require `read_files`, `read_themes` or `read_images` — scopes this app does
    not hold at the time of implementation. Shopify's documentation does not
    mark these fields as requiring a scope.

    Video, Model3d and ExternalVideo have no fragment of their own. They resolve
    through the File fragment, so they return `id`, `typename`, `fileStatus`,
    `alt`, `createdAt` and `updatedAt`, but no type-specific detail.
*/ -}}
{{- $graphqlQuery := `query GetFilesByIds($ids: [ID!]!) {
    nodes(ids: $ids) {
        __typename
        ... on File {
            id
            fileStatus
            alt
            createdAt
            updatedAt
        }
        ... on MediaImage {
            mimeType
            image {
                url
                width
                height
                altText
            }
        }
        ... on GenericFile {
            mimeType
            url
            originalFileSize
        }
    }
}` -}}

{
    "query": {{toJson $graphqlQuery}},
    "variables": {
        "ids": {{toJson .inputs.ids}}
    }
}
