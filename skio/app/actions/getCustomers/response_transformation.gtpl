{{/*
  Transform the StorefrontUsers response for getCustomers.
  - Returns a list of customers, each with their subscriptions.
  - If no users found, returns an empty array.
  - If errors, returns errors.
*/}}

{{if .rawData.errors}}
{
  "errors": {{ toJson .rawData.errors }}
}
{{else if and .rawData.data .rawData.data.StorefrontUsers}}
  {{ toJson .rawData.data.StorefrontUsers }}
{{else}}
  []
{{end}} 