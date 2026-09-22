{{- /* Look up the Skio storefront user from the Gladly customer profile.

       Every value interpolated into the where-clause goes through `toJson`, which emits the
       whole string literal - quotes included, inner quotes and backslashes escaped. GraphQL
       string-literal escaping follows JSON's rules, so this closes the hole the hand-written
       quotes left: a profile value containing a double quote used to close the literal early and
       rewrite the filter. Note there are no quotes in the format strings any more; `toJson`
       supplies them, and adding your own would double them.

       Escaping rather than GraphQL variables on purpose. These columns demonstrably accept
       inline string literals - that is what ships today and works against the live API - and
       escaping preserves that shape exactly. A typed variable would need the Hasura input type
       name, which cannot be verified offline, and a `String!` variable is not equivalent anyway:
       a uuid column takes an inline string literal but rejects a `String!` on type grounds.

       Email matching uses Skio's `emailLower` column against a lower-cased profile address.
       The shipped version matched `email: {_eq:}`, which is case-sensitive: a profile holding
       Jane@Example.com never found a Skio user stored as jane@example.com, and the failure was
       silent - an empty card on an HTTP 200. Still an exact match, so it cannot over-match.

       Every profile field is read through `default ""` first. The shipped version compared
       `.customer.primaryPhoneNumber.number` against "" directly; on a profile with no phone
       that value is nil, nil != "" is true, and the query went out carrying a literal
       `{phoneNumber: {_eq: "%!s(<nil>)"}}` - printf's rendering of nil. Harmless by luck (it
       matches nothing) but it was a malformed filter on every phone-less profile.

       Phone matching stays exact and mobile-only, since a shared landline is not an identity. */ -}}
{{- $primaryEmail := "" -}}
{{- with .customer.primaryEmailAddress -}}{{- $primaryEmail = . | toString | trim -}}{{- end -}}
{{- /* `with` is the only safe way in to a key the profile may omit entirely: both `kindIs`
       and sprig's `default` reach for reflect.Value.Type and panic on a zero Value. */ -}}
{{- $primaryPhone := "" -}}
{{- with .customer.primaryPhoneNumber -}}
    {{- $primaryPhone = .number | default "" | toString | trim -}}
{{- end -}}

{{- /* There is deliberately no separate "profile has no contact info" pre-check. `len` on a key
       the profile omits panics, and the shipped pre-check only survived because Go's `and`
       short-circuited before reaching it - which also meant the check never actually fired, and
       a contactless profile sent Skio `_or: []`. Counting the conditions we actually built is
       nil-safe AND catches more: a profile whose only email is an empty string, or whose only
       numbers are landlines. */ -}}
{{- $conditions := list -}}

{{- /* Emails, lower-cased for emailLower. */ -}}
{{- if ne $primaryEmail "" -}}
    {{- $conditions = append $conditions (printf `{emailLower: {_eq: %s}}` (toJson (lower $primaryEmail))) -}}
{{- end -}}
{{- range $email := .customer.emailAddresses -}}
    {{- $e := "" -}}
    {{- with $email -}}{{- $e = . | toString | trim -}}{{- end -}}
    {{- if ne $e "" -}}
        {{- $conditions = append $conditions (printf `{emailLower: {_eq: %s}}` (toJson (lower $e))) -}}
    {{- end -}}
{{- end -}}

{{- /* Mobile numbers only. */ -}}
{{- if ne $primaryPhone "" -}}
    {{- $conditions = append $conditions (printf `{phoneNumber: {_eq: %s}}` (toJson $primaryPhone)) -}}
{{- end -}}
{{- range $phone := .customer.phoneNumbers -}}
    {{- with $phone -}}
        {{- $n := .number | default "" | toString | trim -}}
        {{- if and (eq (.type | default "" | toString) "MOBILE") (ne $n "") -}}
            {{- $conditions = append $conditions (printf `{phoneNumber: {_eq: %s}}` (toJson $n)) -}}
        {{- end -}}
    {{- end -}}
{{- end -}}

{{- $conditions = uniq $conditions -}}
{{- if eq (len $conditions) 0 -}}
    {{- stop "unable to retrieve customer data since the customer profile does not have any email addresses or mobile phone numbers" -}}
{{- end -}}

{{- $query := printf `
query {
    StorefrontUsers(
        where: {
            _or: [%s]
        },
        limit: 5
    ) {
        id
        email
        emailLower
        firstName
        lastName
        phoneNumber
        platformId
        createdAt
        updatedAt
        redactedAt
        smsTransactionalOptIn
        ShippingAddresses(limit: 10) {
                id
                platformId
                firstName
                lastName
                company
                address1
                address2
                city
                province
                country
                zip
                phoneNumber
                doorCode
        }
    }
}
` (join ", " $conditions) -}}
{
  "query": {{ toJson $query }}
}
