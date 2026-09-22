{{- if or (eq .rawData nil) (eq .rawData.data nil) }}
    {{stop "No Recurly account found for the customer profile email address."}}
{{- else if eq (len .rawData.data) 0 }}
    {{stop "No Recurly account found for the customer profile email address."}}
{{- else if gt (len .rawData.data) 1}}
    {{stop "Recurly returned more than one account for the customer profile email address."}}
{{- else }}
    {{- toJson (index .rawData.data 0) -}}
{{- end}}
