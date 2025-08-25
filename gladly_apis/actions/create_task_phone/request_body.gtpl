{
    "assignee": {
        "inboxId": "{{.inputs.inboxId}}"
        {{- if .inputs.agentId}},
        "agentId": "{{.inputs.agentId}}"
        {{end -}}
    },
    "body": "{{.inputs.task}}",
    "dueAt": "{{.inputs.dueAt}}",
    "customer": {
        "mobilePhone": "{{.inputs.mobilePhone}}"
    }
}