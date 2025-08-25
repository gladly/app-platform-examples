{
    "status": {
        "value": "{{.inputs.status}}"
        {{- if .inputs.force}},
        "force": true
        {{- end}}
    }
}