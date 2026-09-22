{{ if (ne .response.statusCode 200) }}
    {{- $error := toJson .rawData.errors -}}
	{{- print $error | fail -}}
{{- end -}}

{{- $errors := .rawData.errors -}}
{{- $hasErrors := false -}}
{{- if ne $errors nil -}}
    {{- if gt (len $errors) 0 -}}
        {{- $hasErrors = true -}}
    {{- end -}}
{{- end -}}

{{- $owners := list -}}
{{- $notFoundIndices := list -}}
{{- if ne .rawData.data nil -}}
    {{- if ne .rawData.data.nodes nil -}}
        {{- range $i, $node := .rawData.data.nodes -}}
            {{- if eq $node nil -}}
                {{- $notFoundIndices = append $notFoundIndices $i -}}
            {{- else -}}
                {{- $mfs := list -}}
                {{- if ne $node.metafields nil -}}
                    {{- if ne $node.metafields.nodes nil -}}
                        {{- range $node.metafields.nodes -}}
                            {{- /* Shopify can return `key` namespace-prefixed (e.g.
                                 `custom.material`); normalize to the bare key. */ -}}
                            {{- $key := trimPrefix (printf "%s." .namespace) .key -}}
                            {{- $mf := dict
                                "id" .id
                                "createdAt" .createdAt
                                "namespace" .namespace
                                "key" $key
                                "value" .value
                                "type" .type
                                "ownerId" $node.id
                                "ownerType" .ownerType -}}
                            {{- $mfs = append $mfs $mf -}}
                        {{- end -}}
                    {{- end -}}
                {{- end -}}
                {{- if gt (len $mfs) 0 -}}
                    {{- $owner := dict "id" $node.id "typename" $node.__typename "metafields" $mfs -}}
                    {{- $owners = append $owners $owner -}}
                {{- end -}}
            {{- end -}}
        {{- end -}}
    {{- end -}}
{{- end -}}

{{- $errorList := list -}}
{{- if $hasErrors -}}
    {{- range $errors -}}
        {{- $errorList = append $errorList (dict "message" .message "code" .extensions.code) -}}
    {{- end -}}
{{- end -}}
{{- range $notFoundIndices -}}
    {{- $missingId := index $.inputs.ownerIds . -}}
    {{- $errorList = append $errorList (dict "message" (printf "Owner not found: %s" $missingId) "code" "not_found") -}}
{{- end -}}

{{- $output := dict "owners" $owners "errors" (ternary $errorList nil (gt (len $errorList) 0)) -}}
{{- toJson $output -}}
