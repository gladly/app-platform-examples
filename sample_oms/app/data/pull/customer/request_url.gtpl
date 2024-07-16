{{- /* Don't process the template if customer has no email addresses. */}}
{{- if eq (len .customer.emailAddresses) 0}}
	{{- stop "unable to retrieve customer data since the customer profile does not have any email addresses"}}
{{- end}}

{{- $email := ""}}
{{- if .customer.primaryEmailAddress}}
	{{- /* Check if primary email address is set */}}
	{{- $email = .customer.primaryEmailAddress}}
{{- else if gt (len .customer.emailAddresses) 0 }}
	{{- /* Check if there's an email in the emailAddresses array. */}}
	{{- $email = index .customer.emailAddresses 0 }}
{{- end}}
https://gladly-sample-oms.vercel.app/api/customers?emailAddress={{urlquery $email}}