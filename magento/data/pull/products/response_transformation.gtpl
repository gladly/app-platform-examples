{{- $products := list -}}
{{- range .rawData.items -}}
    {{- $urlKey := "" -}}
    {{- range .custom_attributes -}}
        {{- if eq .attribute_code "url_key" -}}
            {{- $urlKey = .value -}}
        {{- end -}}
    {{- end -}}
    {{- $imageFilePath := "" -}}
    {{- range .media_gallery_entries -}}
        {{- if and (not .disabled) (not $imageFilePath) -}}
            {{- $imageFilePath = .file -}}
        {{- end -}}
    {{- end -}}
    {{- $products = append $products (dict "sku" .sku "urlKey" $urlKey "imageFilePath" $imageFilePath) -}}
{{- end -}}
{{- $products | toJson -}}