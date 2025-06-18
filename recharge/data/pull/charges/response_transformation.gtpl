{{- $hasCharges := and (ne .rawData nil) (ne .rawData.charges nil) (gt (len .rawData.charges) 0) -}}

{{- if $hasCharges -}}
[
    {{- range $index, $charge := .rawData.charges -}}
        {{- /* create a map with formatted charge IDs as strings to ensure proper graphql output formatting */ -}}
        {{- $formattedChargeIds := dict
            "id" ($charge.id | int64 | toString)
            "address_id" ($charge.address_id | int64 | toString)
            "customer" (dict
                "id" ($charge.customer.id | int64 | toString))
        -}}

        {{- /* convert all line items purchase_item_ids to strings to ensure proper graphql output formatting */ -}}
        {{- range $index, $line_item := $charge.line_items -}}
            {{- $_ := set . "purchase_item_id" ($line_item.purchase_item_id | int64 | toString) -}}
        {{- end -}}

        {{- /* merge the formatted IDs back into the charge data */}}
        {{- $charge = mergeOverwrite $charge $formattedChargeIds -}}

        {{- $charge | toJson -}}
        {{- if lt (add $index 1) (len $.rawData.charges) -}},{{- end -}}
    {{- end -}}
]
{{- else -}}
[]
{{- end -}}