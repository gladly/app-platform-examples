{{if or (eq .response.statusCode 200) (eq .response.statusCode 400)}}
{{toJson .rawData}}
{{else}}
    {{printf "unexpected response status code: %d" .response.statusCode | fail }}
{{end}}