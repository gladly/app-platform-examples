{{- /* Look up storefront user by email or phone number */ -}}
{{- if and (eq .customer.primaryEmailAddress "") (eq .customer.primaryPhoneNumber.number "") (eq (len .customer.emailAddresses) 0) (eq (len .customer.phoneNumbers) 0) }}
	{{- stop "unable to retrieve customer data since the customer profile does not have any email addresses or phone numbers"}}
{{- end}}

{{- /* Create conditions array */ -}}
{{- $conditions := list }}

{{- /* Add email conditions */ -}}
{{- if ne .customer.primaryEmailAddress "" }}
	{{- $conditions = append $conditions (printf `{email: {_eq: "%s"}}` .customer.primaryEmailAddress) }}
{{- end }}
{{- range .customer.emailAddresses }}
	{{- if ne . "" }}
		{{- $conditions = append $conditions (printf `{email: {_eq: "%s"}}` .) }}
	{{- end }}
{{- end }}

{{- /* Add phone conditions */ -}}
{{- if ne .customer.primaryPhoneNumber.number "" }}
	{{- $conditions = append $conditions (printf `{phoneNumber: {_eq: "%s"}}` .customer.primaryPhoneNumber.number) }}
{{- end }}
{{- range .customer.phoneNumbers }}
	{{- if and (eq .type "MOBILE") (ne .number "") }}
		{{- $conditions = append $conditions (printf `{phoneNumber: {_eq: "%s"}}` .number) }}
	{{- end }}
{{- end }}

{{$query := printf `
query {
    StorefrontUsers(
        where: {
            _or: [%s]
        }
    ) {
        id
        email
        firstName
        lastName
        phoneNumber
        platformId
        createdAt
        updatedAt
    }
}
` (join ", " $conditions) }}

{
    "query": {{toJson $query}}
} 