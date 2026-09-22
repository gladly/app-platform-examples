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

{{- $files := list -}}
{{- $notFoundIndices := list -}}

{{- if ne .rawData.data nil -}}
    {{- if ne .rawData.data.nodes nil -}}
        {{- range $i, $node := .rawData.data.nodes -}}
            {{- if eq $node nil -}}
                {{- /* Shopify returns an unresolvable GID as a positional null. */ -}}
                {{- $notFoundIndices = append $notFoundIndices $i -}}
            {{- else if hasKey $node "id" -}}
                {{- /* `id` is selected inside the `... on File` fragment, so its
                     presence is what identifies a File — and it holds for every
                     File type, including ones this action does not break out. */ -}}
                {{- $file := dict
                    "id" $node.id
                    "typename" $node.__typename
                    "fileStatus" $node.fileStatus
                    "alt" $node.alt
                    "createdAt" $node.createdAt
                    "updatedAt" $node.updatedAt -}}
                {{- /* Then the type-specific arm, one per concrete File type.
                     Video, Model3d and ExternalVideo match none, so they carry the
                     interface fields and no arm. */ -}}
                {{- if eq $node.__typename "MediaImage" -}}
                    {{- $mediaImage := dict "mimeType" $node.mimeType -}}
                    {{- /* Shopify returns no `image` until `fileStatus` is READY, so
                         the arm is populated but the image itself may be absent. */ -}}
                    {{- if ne $node.image nil -}}
                        {{- $_ := set $mediaImage "image" (dict
                            "url" $node.image.url
                            "width" $node.image.width
                            "height" $node.image.height
                            "altText" $node.image.altText) -}}
                    {{- end -}}
                    {{- $_ := set $file "mediaImage" $mediaImage -}}
                {{- else if eq $node.__typename "GenericFile" -}}
                    {{- $_ := set $file "genericFile" (dict
                        "url" $node.url
                        "originalFileSize" $node.originalFileSize
                        "mimeType" $node.mimeType) -}}
                {{- end -}}
                {{- $files = append $files $file -}}
            {{- else -}}
                {{- /* The GID resolved, but to something that is not a File (e.g. an
                     Order GID), so it matched no fragment and carries only
                     `__typename`. Reported as not found, same as an unresolvable id. */ -}}
                {{- $notFoundIndices = append $notFoundIndices $i -}}
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
    {{- $missingId := index $.inputs.ids . -}}
    {{- $errorList = append $errorList (dict "message" (printf "File not found: %s" $missingId) "code" "not_found") -}}
{{- end -}}

{{- $output := dict "files" $files "errors" (ternary $errorList nil (gt (len $errorList) 0)) -}}
{{- toJson $output -}}
