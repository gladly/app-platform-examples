
{{- if .rawData.errors -}}
    {{/* make single string from errors. format: <field> - <message>; ... */}}
    {{- $errorMessages := "" -}}
    {{- range $index, $error := .rawData.errors -}}
        {{- $field := $error.message -}}
        {{- if $index -}}
            {{- $errorMessages = (print $errorMessages "; " $field " - " $error.extensions.code) -}}
        {{- else -}}
            {{- $errorMessages = (print $field " - " $error.extensions.code) -}}
        {{- end -}}
    {{- end -}}

    {{- fail $errorMessages -}}
{{- else -}}
{{- $metafields := list -}}
{{- $allNodes := list -}}

{{- /* Collect all nodes (customer and orders) */ -}}
{{- if .rawData.data.customer -}}
{{- /* Add customer node */ -}}
{{- $allNodes = append $allNodes (dict "node" .rawData.data.customer "metafields" .rawData.data.customer.metafields) -}}

{{- /* Add order nodes */ -}}
{{- if .rawData.data.customer.orders.nodes -}}
{{- range .rawData.data.customer.orders.nodes -}}
{{- $allNodes = append $allNodes (dict "node" . "metafields" .metafields) -}}
{{- end -}}
{{- end -}}
{{- end -}}

{{- /* Process metafields for all nodes */ -}}
{{- range $allNodes -}}
{{- /* The container (customer or order) is the metafield owner */ -}}
{{- $ownerId := .node.id -}}
{{- if .metafields.nodes -}}
{{- range .metafields.nodes -}}
{{- $metafield := dict
    "id" .id
    "createdAt" .createdAt
    "namespace" .namespace
    "key" .key
    "value" .value
    "type" .type
    "ownerId" $ownerId
    "ownerType" .ownerType -}}
{{- $metafields = append $metafields $metafield -}}
{{- end -}}
{{- end -}}
{{- end -}}

{{- toJson $metafields -}}
{{- end -}}
