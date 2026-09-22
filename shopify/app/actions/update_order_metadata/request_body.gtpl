{{- /*
    update_order_metadata wraps Shopify's orderUpdate mutation, exposing only the
    fields we want the agent to be able to write (currently tags and note).

    Semantics (mirrors Shopify):
      - tags omitted (or nil)   -> "tags" key is NOT included in the GraphQL input;
                                   Shopify leaves the existing tags unchanged.
      - tags: ["a","b"]         -> "tags": ["a","b"] is forwarded; Shopify REPLACES
                                   the full set (set semantics, last-write-wins).
      - tags: []                -> "tags": [] is forwarded explicitly; this is how
                                   the agent clears all tags. The empty array MUST
                                   reach Shopify — do not strip it.

    note follows the same shape: omitted -> untouched, "" -> cleared, "x" -> set.

    Validation: at least one of tags or note must be provided (present and
    non-nil). If BOTH are absent/nil the action would have nothing to write, so
    we stop. Note that "provided" includes the clearing values tags: [] and
    note: "" -- those are intentional operations and MUST NOT trigger the stop.
*/ -}}
{{- $hasTags := and (hasKey .inputs "tags") (ne .inputs.tags nil) -}}
{{- $hasNote := and (hasKey .inputs "note") (ne .inputs.note nil) -}}
{{- if and (not $hasTags) (not $hasNote) -}}
    {{- stop "At least one of tags or note must be provided." -}}
{{- end -}}
{{- $query := `mutation orderUpdate($input: OrderInput!) {
  orderUpdate(input: $input) {
    order {
      id
      tags
      note
      updatedAt
    }
    userErrors {
      field
      message
    }
  }
}` -}}
{{- $input := dict "id" .inputs.orderId -}}
{{- if $hasTags -}}
    {{- $input = set $input "tags" .inputs.tags -}}
{{- end -}}
{{- if $hasNote -}}
    {{- $input = set $input "note" .inputs.note -}}
{{- end -}}
{
    "query": {{ toJson $query }},
    "variables": {{ toJson (dict "input" $input) }}
}
