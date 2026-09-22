{{- /* Find Skio customers by email or phone.

       Caller-supplied values are escaped with toJson, which emits a properly quoted and
       escaped JSON string literal; GraphQL string-literal escaping follows JSON rules, so
       the value cannot break out of the literal. The shipped version wrote its own quotes
       around a printf %s, so a double quote in the email or phone rewrote the where-clause
       and could return customers the caller never asked for.

       The values stay inline string literals rather than moving to the `variables` object.
       Skio's columns here accept an inline string literal - that is what the live query
       does today - and a `String!` variable is not equivalent: a uuid column takes an
       inline string literal but rejects a String! variable on type grounds. Escaping keeps
       the shape that is known to work.

       Email matches Skio's `emailLower` column against a lower-cased input. The shipped
       version matched `email: {_eq:}`, which is case-sensitive: an agent (or Gladly AI)
       passing Jane@Example.com never found a user stored as jane@example.com, and got a
       clean empty list rather than an error. Still an exact match, so it cannot over-match.

       Both list selections are bounded - the shipped version asked for StorefrontUsers and
       their Subscriptions with no limit at all, against Skio's 100-node response cap. */ -}}
{{- $conditions := list -}}
{{- if .inputs.email -}}
    {{- $email := .inputs.email | toString | trim -}}
    {{- if ne $email "" -}}
        {{- $conditions = append $conditions (printf `{emailLower: {_eq: %s}}` (toJson (lower $email))) -}}
    {{- end -}}
{{- end -}}
{{- if .inputs.phone -}}
    {{- $phone := .inputs.phone | toString | trim -}}
    {{- if ne $phone "" -}}
        {{- $conditions = append $conditions (printf `{phoneNumber: {_eq: %s}}` (toJson $phone)) -}}
    {{- end -}}
{{- end -}}
{{- /* Never send `_or: []` - it is a filter that matches nothing and reads like a bug. */ -}}
{{- if eq (len $conditions) 0 -}}
    {{- stop "Supply an email address or a phone number to look up." -}}
{{- end -}}
{{- $query := printf "\nquery {\n  StorefrontUsers(where: {_or: [%s]}, limit: 5) {\n    id\n    email\n    firstName\n    lastName\n    phoneNumber\n    platformId\n    createdAt\n    updatedAt\n    Subscriptions(limit: 10) {\n      id\n      status\n      statusContext\n      createdAt\n      updatedAt\n      storefrontUserId\n      currencyCode\n      nextBillingDate\n      platformId\n      cyclesCompleted\n      cancelledAt\n      BillingPolicy {\n        id\n        interval\n        intervalCount\n        isMaxCycleV2\n        maxCycles\n        minCycles\n      }\n      DeliveryPolicy {\n        id\n        interval\n        intervalCount\n        isMaxCycleV2\n        maxCycles\n        minCycles\n      }\n    }\n  }\n}\n" (join ", " $conditions) -}}
{
  "query": {{ toJson $query }}
}
