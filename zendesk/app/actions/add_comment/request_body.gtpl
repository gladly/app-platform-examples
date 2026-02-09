{
  "ticket": {
    "comment": {
      {{if .inputs.comment.html_body}}"html_body": "{{.inputs.comment.html_body}}"{{else}}"body": "{{.inputs.comment.body}}"{{end}},
      "public": {{.inputs.comment.public}}{{if .inputs.comment.author_id}},
      "author_id": {{.inputs.comment.author_id}}{{end}}
    },
    "safe_update": true,
    "updated_stamp": "{{ now | date "2006-01-02T15:04:05Z" }}"{{if .inputs.channel}},
    "via": {"channel": "{{.inputs.channel}}"}{{end}}
  }
}
