{
    "assignee": {
        "inboxId": "{{.inputs.inboxId}}"
        {{- if .inputs.agentId}},
        "agentId": "{{.inputs.agentId}}"
        {{end -}}
    },
    "body": "{{.inputs.task}}",
    "dueAt": "{{date "2006-01-02T15:04:05Z07:00" .inputs.dueAt}}",
    "customer": {
        "emailAddress": "{{.inputs.emailAddress}}"
    }
}