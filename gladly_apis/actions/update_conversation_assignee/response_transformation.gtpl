{{if eq .response.statusCode 204 -}}
{
    "success": true
}
{{else if or (eq .response.statusCode 400) (eq .response.statusCode 404)}}
{
    "success": false,
    "errors": {{toJson .rawData.errors}}
}
{{else}}
    {{printf "update conversation assignee request failed with unexpected status code: %d" .response.statusCode | fail}}
{{end}}