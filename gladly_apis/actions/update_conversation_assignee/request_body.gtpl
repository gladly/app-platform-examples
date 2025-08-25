{
    "assignee": {
        "inboxId": "{{.inputs.inboxId}}"
        {{- if .inputs.agentId}},
        "agentId": "{{.inputs.agentId}}"
        {{- end}}
    }
}