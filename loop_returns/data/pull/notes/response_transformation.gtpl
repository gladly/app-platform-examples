{{- /* Successful response main transformation */ -}}
[
	{{- /* We need the return ID associated with the note. Loop Returns does not provide the parent */}}
	{{- /* return ID with the note object so we need to retrieve it from the URL that was used */}}
	{{- /* to retrieve the note. The loop returns note request URL looks as follows: */}}
	{{- /* https://api.loopreturns.com/api/v1/warehouse/return/{{.id}}/notes */}}
	{{- $returnId := mustRegexFind "return/\\d+" .request.url | trimPrefix "return/"}}
	{{- range $dataIndex, $note := .rawData}}
		{{- /* prepend comma to object definition if not first object */}}
		{{- if gt $dataIndex 0}},{{end}}
      {
        "returnId":"{{$returnId}}",
        "id": "{{int64 $note.id}}",
        "content": {{toJson $note.content}},
        "createdAt": {{template "normalizeTimestamp" $note.created_at}}
    }
	{{end -}}
]

{{- /* Convert any incoming timestamps to an ISO8601 format */}}
{{- define "normalizeTimestamp"}}
	{{- if not . -}}
		null
	{{- else}}
		{{- if regexMatch "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}" . -}}
			"{{printf "%sZ" (replace " " "T" .)}}"
		{{- else}}
			{{- $timeStamp := toDate "2006-01-02T15:04:05Z07:00" .}}
			{{- if not $timeStamp.IsZero -}}
				"{{.}}"
			{{- else -}}
				null
			{{- end}}
		{{- end}}
	{{- end}}
{{- end}}