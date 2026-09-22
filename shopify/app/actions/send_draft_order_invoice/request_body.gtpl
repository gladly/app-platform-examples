{{ if or (eq .inputs.draftOrderId nil) (eq .inputs.draftOrderId "") }}
	{{ stop "Draft order ID is required."}}
{{ end }}

{{- /*
    email is optional. When a non-empty recipient is provided it is forwarded as
    Shopify's EmailInput.to, overriding the recipient for THIS send only. When email
    is omitted/nil/empty, the request is byte-for-byte identical to the
    default-recipient behavior: the mutation takes only $id and no email variable.
*/ -}}
{{- $hasEmail := and (hasKey .inputs "email") (ne .inputs.email nil) (ne .inputs.email "") -}}

{{- $variables := dict "id" .inputs.draftOrderId -}}

{{- $query := `
mutation draftOrderInvoiceSend($id: ID!) {
    draftOrderInvoiceSend(id: $id) {
        draftOrder {
            id
            name
            status
            invoiceUrl
            invoiceSentAt
        }
        userErrors {
            field
            message
        }
    }
}
` -}}

{{- if $hasEmail -}}
{{- $variables = set $variables "email" (dict "to" .inputs.email) -}}
{{- $query = `
mutation draftOrderInvoiceSend($id: ID!, $email: EmailInput) {
    draftOrderInvoiceSend(id: $id, email: $email) {
        draftOrder {
            id
            name
            status
            invoiceUrl
            invoiceSentAt
        }
        userErrors {
            field
            message
        }
    }
}
` -}}
{{- end -}}

{{- $payload := dict "query" $query "variables" $variables -}}
{{- toJson $payload -}}
