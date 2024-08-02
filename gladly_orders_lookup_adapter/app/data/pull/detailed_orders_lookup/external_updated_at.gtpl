{{- $iso8601Date := now -}}
{{$dateString := .createdAt -}}
{{if kindIs "string" $dateString -}}
	{{$timeFormats := splitList ";" "2006-01-02T15:04:05Z07:00;2006-01-02T15:04:05.000Z07:00;2006-01-02T15:04:05;2006-01-02" -}}
	{{range $index, $timeFormat := $timeFormats -}}
		{{$date := toDate $timeFormat $dateString -}}
		{{if not $date.IsZero -}}
			{{$iso8601Date = $date -}}
			{{break -}}
		{{end -}}
	{{end -}}
	{{dateInZone "2006-01-02T15:04:05Z07:00" $iso8601Date "UTC"}}
{{end -}}