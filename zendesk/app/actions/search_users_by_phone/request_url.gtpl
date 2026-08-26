{{- /*
  Search for Zendesk users by phone number.
  Excludes suspended users to return only active accounts.

  Phone number format (from Zendesk docs):
  https://support.zendesk.com/hc/en-us/articles/4408886879258-Zendesk-Support-search-reference
  "When searching by phone number, you must include a plus sign (+) before the number.
  When searching by phone number using the API, you must include %2B (the URL-encoded
  version of +) before the number (for example, /api/v2/search?query=type:phone:%2B555-111-2222)."

  Note: We manually URL-encode the query instead of using urlquery on the full string
  because urlquery encodes spaces as + (application/x-www-form-urlencoded style),
  but Zendesk requires spaces encoded as %20 (RFC 3986 percent-encoding).
  There is currently no built-in template function for RFC 3986 encoding.
*/ -}}
{{- $phone := .inputs.phone -}}
{{- /* Ensure phone starts with + for Zendesk search */ -}}
{{- if not (hasPrefix "+" $phone) -}}
{{- $phone = printf "+%s" $phone -}}
{{- end -}}
https://{{.integration.configuration.subdomain}}.zendesk.com/api/v2/users/search?query=phone%3A{{urlquery $phone}}%20is_suspended%3Afalse
