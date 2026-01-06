{{- /* Validate that we have a customer with at least one email address before proceeding. */}}
{{- if not .customer}}
	{{- stop "unable to retrieve order tracking data since no customer context was provided"}}
{{- end}}
{{- $emails := .customer.emailAddresses}}
{{- if or (not $emails) (eq (len $emails) 0)}}
	{{- stop "unable to retrieve order tracking data since the customer profile does not have any email addresses"}}
{{- end}}

https://api.wonderment.com/graphql