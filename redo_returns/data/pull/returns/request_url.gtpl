{{- /* Don't process the template if customer has no email addresses. */}}
{{- if eq (len .customer.emailAddresses) 0}}
	{{- stop "unable to retrieve redo return data since the customer profile does not have any email addresses"}}
{{- end}}

{{- /* Use the primary email address if available, otherwise use the first email address. */}}
{{- $email := index .customer.emailAddresses 0}}
{{- if .customer.primaryEmailAddress}}
	{{- $email = .customer.primaryEmailAddress}}
{{- end}}

https://api.getredo.com/v2.2/stores/{{.integration.configuration.storeId}}/returns?email={{urlquery $email}}
