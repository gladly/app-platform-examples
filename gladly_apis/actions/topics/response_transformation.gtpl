{{if eq .response.statusCode 200}}
    {{if .inputs.enabledOnly}}
        {{$enabledTopics := 0}}
        [
        {{range .rawData}}
            {{if not .disabled}}
                {{if gt $enabledTopics 0}},{{end}}
                {{toJson .}}
                {{$enabledTopics = add1 $enabledTopics}}
            {{end}}
        {{end}}
        ]
    {{else}}
        {{toJson .rawData}}
    {{end}}
{{else}}
    {{printf "topic list request failed with status code: %d" .response.statusCode | fail}}
{{end}}