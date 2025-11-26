{{- /* Ensure we have customer context with email addresses before building the request. */}}
{{- if not .customer}}
  {{- stop "unable to retrieve wonderment order data since no customer context was provided"}}
{{- end}}
{{- $emails := .customer.emailAddresses}}
{{- if or (not $emails) (eq (len $emails) 0)}}
  {{- stop "unable to retrieve wonderment order data since the customer profile does not have any email addresses"}}
{{- end}}
{
  "query": "query fetchGladlyOrderInformation($emails: [String!]!) { orders(input: {customerEmails: $emails}) { name shipments { trackingCode status carrierDisplayName } } }",
  "variables": {
    "emails": {{ toJson $emails }}
  }
}