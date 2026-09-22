{
  "customer": {
    "mobilePhone": "{{.inputs.mobilePhone}}"
  },
  "content": {
    "type": "CUSTOMER_ACTIVITY",
    "activityType": "{{.inputs.activityType}}",
    "title": "{{.inputs.title}}",
    "body": "{{.inputs.body}}",
    "sourceName": "{{.inputs.sourceName}}",
    "link": {
      "url": "{{.inputs.link.url}}",
      "text": "{{.inputs.link.text}}"
    }
  }
}
