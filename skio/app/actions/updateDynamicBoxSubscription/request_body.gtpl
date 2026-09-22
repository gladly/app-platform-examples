{{- /* Build-a-Box edit. `updated` is the box's new full contents; `original` is optional and
       lets Skio diff against what the customer had. No other helpdesk integration edits bundles. */ -}}
{{- $box := dict "boxId" (.inputs.boxId | toString) "boxIndex" (.inputs.boxIndex | toString) -}}
{{- if and (ne .inputs.boxSectionId nil) (ne (.inputs.boxSectionId | toString | trim) "") -}}
    {{- $box = set $box "boxSectionId" (.inputs.boxSectionId | toString) -}}
{{- end -}}
{{- $updated := list -}}
{{- range $item := .inputs.updated -}}
    {{- $updated = append $updated (dict "variantId" ($item.variantId | toString) "quantity" ($item.quantity | int)) -}}
{{- end -}}
{{- if eq (len $updated) 0 -}}
    {{- stop "The box's new contents are empty. Supply at least one product and quantity." -}}
{{- end -}}
{{- $input := dict "subscriptionId" (.inputs.subscriptionId | toString) "dynamicBox" $box "updated" $updated -}}
{{- if and (ne .inputs.original nil) (gt (len .inputs.original) 0) -}}
    {{- $original := list -}}
    {{- range $item := .inputs.original -}}
        {{- $original = append $original (dict "variantId" ($item.variantId | toString) "quantity" ($item.quantity | int)) -}}
    {{- end -}}
    {{- $input = set $input "original" $original -}}
{{- end -}}
{
  "query": "mutation updateDynamicBoxSubscription($input: UpdateDynamicBoxSubscriptionInput!) { updateDynamicBoxSubscription(input: $input) { ok } }",
  "variables": {
    "input": {{ toJson $input }}
  }
}
