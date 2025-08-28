{{- /* Don't process the template if customer has no email addresses. */}}
{{- if eq (len .customer.emailAddresses) 0}}
	{{- stop "unable to retrieve loop return data since the customer profile does not have any email addresses"}}
{{- end}}

{{- $emailAddresses := .customer.emailAddresses}}
{{- $primaryEmailAddress := .customer.primaryEmailAddress}}
{{- if $primaryEmailAddress}}
	{{- /* Create list of email addresses with primary email address at the front since we always want to include the primary email address. */}}
	{{- $emailAddresses = mustPrepend $emailAddresses $primaryEmailAddress | mustUniq}}
{{- end}}

{{- /* We will retrieve returns for up to 5 email addresses. */}}
{{- $emailAddresses = mustSlice $emailAddresses 0 (min (len $emailAddresses) 5)}}

{{- /* Retrieve returns that are a maximum of 1 year old. */}}
{{- $updatedAt := now | dateModify "-8760h" | date "2006-01-02"}}

{{- range $emailAddresses}}
https://api.loopreturns.com/api/v2/returns?filter=updated_at&limit=10&from={{$updatedAt}}&customer_email={{urlquery .}}
{{- end}}