{
    "to": [
        {
            "email": {{toJson .inputs.toEmail}}
            {{- if .inputs.toName}},
            "name": {{toJson .inputs.toName}}
            {{- end}}
        }
    ],
    "from": {{toJson .inputs.from}},
    "subject": {{toJson .inputs.subject}},
    "body": {
        "html": {{toJson .inputs.bodyHtml}},
        "text": {{toJson .inputs.bodyText}}
    }
}
