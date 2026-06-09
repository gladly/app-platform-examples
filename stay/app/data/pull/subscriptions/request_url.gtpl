{{- $email := "" -}}
{{- if .customer.primaryEmailAddress -}}
	{{- $email = .customer.primaryEmailAddress -}}
{{- else -}}
	{{- range .customer.emailAddresses -}}
		{{- if eq $email "" -}}{{- $email = . -}}{{- end -}}
	{{- end -}}
{{- end -}}

{{- if eq $email "" -}}
	{{- stop "unable to retrieve Stay.ai subscriptions since the customer profile does not have any email addresses" -}}
{{- end -}}

{{- printf `https://api.retextion.com/api/v2/subscriptions/?email=%s&pageSize=100&sortBy=updatedAt&sortDirection=DESC` ($email | urlquery) -}}
