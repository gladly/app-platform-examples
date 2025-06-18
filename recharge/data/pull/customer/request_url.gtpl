{{- if eq (len .customer.emailAddresses) 0}}
	{{- stop "unable to retrieve Recharge customer data since the customer profile does not have any email addresses"}}
{{- end}}

{{- $email := ""}}
{{- if .customer.primaryEmailAddress}}
	{{- $email = .customer.primaryEmailAddress}}
{{- else if gt (len .customer.emailAddresses) 0 }}
	{{- $email = index .customer.emailAddresses 0 }}
{{- end}}

{{- printf `https://api.rechargeapps.com/customers?email=%s` ($email | urlquery) -}}
