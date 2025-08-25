{
    "success": {{if eq .response.statusCode 201}}true{{else}}false{{end}}
    {{- if eq .response.statusCode 400}},
    "errors": {{toJson .rawData.errors}}
    {{- end}}
}