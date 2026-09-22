{{- $hasAccounts := and (ne .rawData nil) (ne .rawData.credit_accounts nil) (gt (len .rawData.credit_accounts) 0) -}}

{{- if $hasAccounts -}}
[
    {{- range $index, $account := .rawData.credit_accounts -}}
        {{- /* stringify int64 ids for graphql ID! output */ -}}
        {{- $formattedIds := dict
            "id" ($account.id | int64 | toString)
            "customer_id" ($account.customer_id | int64 | toString)
        -}}
        {{- $account = mergeOverwrite $account $formattedIds -}}
        {{- $account | toJson -}}
        {{- if lt (add $index 1) (len $.rawData.credit_accounts) -}},{{- end -}}
    {{- end -}}
]
{{- else -}}
[]
{{- end -}}
