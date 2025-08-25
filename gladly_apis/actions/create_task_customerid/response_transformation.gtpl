{
    "success": {{if eq .response.statusCode 201}}true{{else}}false{{end}}
    {{- if ne .response.statusCode 201}},
    "errors": {{toJson .rawData.errors}}
    {{- end}}
}