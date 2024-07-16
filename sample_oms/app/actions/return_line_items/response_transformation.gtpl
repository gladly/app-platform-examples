{{- if eq .response.statusCode 200 }}
{{- toJson .rawData -}}
{{- else if eq .response.statusCode 400 }}
  {{- $failedItemsMessage := ""}}
  {{- $hasMultiple := gt (len .rawData.items) 1}}
  {{- range $index, $item := .rawData.items -}}
    {{- $errorMessage := cat "Line item" $item.lineItem "failed to return, reason:" $item.message}}
    {{- $failedItemsMessage = cat $failedItemsMessage $errorMessage}}
    {{- if $hasMultiple}}
      {{- $failedItemsMessage = cat $failedItemsMessage ";"}}
    {{- end}}
  {{- end}}
{
  "status": "error",
  "message": "{{len .rawData.items}} line items failed to return.{{$failedItemsMessage}}"
}
{{- end}}