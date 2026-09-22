{{- /* Ordergroove defines NO 200 response body for this endpoint, so there is nothing
       to echo back - the result object is synthesised from the HTTP status. */ -}}
{{- if and .response (ne .response.statusCode 200) (ne .response.statusCode 204) -}}
  {{- if eq .response.statusCode 404 -}}
    {{ stop (printf "Item %s does not exist in Ordergroove, so there is nothing to remove." .inputs.itemId) }}
  {{- end -}}
  {{- if eq .response.statusCode 400 -}}
    {{- if .rawData.detail -}}
      {{ stop (printf "Remove item: %s" .rawData.detail) }}
    {{- end -}}
    {{ stop "Remove item: Ordergroove rejected the removal. Items cannot be removed from a prepaid subscription." }}
  {{- end -}}
  {{- /* Ordergroove 500s with an EMPTY BODY when the item's parent order is no longer
         open for changes. Observed live: an order that was UNSENT when the card loaded
         was placed, failed payment and became REJECTED minutes later; removing an item
         from it returned 500, not 400. The card cannot prevent this - `isActionable` is
         computed when the pull runs, and these orders churn continuously - so the only
         honest fix is to say so instead of surfacing "failed: 500" with nothing after
         it. Anything else 5xx gets the same treatment; it is still a vendor-side error
         the agent cannot act on except by retrying. */ -}}
  {{- if ge .response.statusCode 500 -}}
    {{ stop "Remove item: Ordergroove would not change this order. It is usually because the order is no longer open - it may have been placed, rejected or skipped since this card was loaded. Refresh the customer's profile to see the current orders, then try again." }}
  {{- end -}}
  {{ fail (printf "Remove item failed: %d %s" .response.statusCode .response.body) }}
{{- end -}}
{
  "deleted": true,
  "itemId": "{{.inputs.itemId}}",
  "message": "Item {{.inputs.itemId}} was removed from the order. Refresh the card to confirm - Ordergroove returns no response body for this operation."
}
