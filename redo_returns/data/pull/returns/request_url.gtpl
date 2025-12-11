{{- range .customer.emailAddresses}}
https://api.getredo.com/v2.2/stores/{{$.integration.configuration.storeId}}/returns?email={{urlquery .}}
{{- end}}