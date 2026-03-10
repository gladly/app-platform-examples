{{- $skus := dict -}}
{{- range .externalData.order -}}
    {{- range .items -}}
        {{- $_ := set $skus .sku true -}}
    {{- end -}}
{{- end -}}
{{- $skuList := keys $skus | join "," -}}
{{- if not $skuList -}}
    {{- stop "no product SKUs to fetch" -}}
{{- end -}}
https://{{$.integration.configuration.host}}/rest/V1/products?searchCriteria[filter_groups][0][filters][0][field]=sku&searchCriteria[filter_groups][0][filters][0][value]={{urlquery $skuList}}&searchCriteria[filter_groups][0][filters][0][condition_type]=in&searchCriteria[pageSize]={{len $skus}}