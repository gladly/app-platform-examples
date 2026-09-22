{{ if or (eq .inputs.orderId nil) (eq .inputs.orderId "") }}
	{{ stop "Order ID is required."}}
{{ end }}

{{- /*
    email is optional. When a non-empty recipient is provided it is forwarded as
    Shopify's EmailInput.to, overriding the order's default recipient. When email
    is omitted/nil/empty, the request is byte-for-byte identical to the
    default-recipient behavior: the mutation takes only $id and no email variable.
*/ -}}
{{- $hasEmail := and (hasKey .inputs "email") (ne .inputs.email nil) (ne .inputs.email "") -}}

{{- $variables := dict "id" .inputs.orderId -}}

{{- $query := `
mutation orderInvoiceSend($id: ID!) {
    orderInvoiceSend(id: $id) {
        order {
            id
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
mutation orderInvoiceSend($id: ID!, $email: EmailInput) {
    orderInvoiceSend(id: $id, email: $email) {
        order {
            id
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
