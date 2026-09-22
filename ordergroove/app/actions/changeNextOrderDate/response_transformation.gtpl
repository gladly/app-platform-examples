{{- /* Error handling. Ordergroove returns 404 for an unknown subscription and 400
       for a validation failure, either as {"detail": "..."} or as a per-field map. */ -}}
{{- if and .response (ne .response.statusCode 200) -}}
  {{- if eq .response.statusCode 404 -}}
    {{ stop (printf "Subscription %s does not exist in Ordergroove." .inputs.subscriptionId) }}
  {{- end -}}
  {{- if eq .response.statusCode 400 -}}
    {{- if .rawData.detail -}}
      {{ stop (printf "%s: %s" "Change next order date" .rawData.detail) }}
    {{- end -}}
    {{- range $field, $msgs := .rawData -}}
      {{- if kindIs "slice" $msgs -}}
        {{- if gt (len $msgs) 0 -}}
          {{ stop (printf "%s: %s" "Change next order date" (join ", " $msgs)) }}
        {{- end -}}
      {{- end -}}
    {{- end -}}
    {{ stop (printf "%s: Ordergroove rejected the change." "Change next order date") }}
  {{- end -}}
  {{ fail (printf "%s failed: %d %s" "Change next order date" .response.statusCode .response.body) }}
{{- end -}}
{{- /* Success. Emit the subscription with only the date coercions the schema needs:
       Ordergroove returns start_date as YYYY-MM-DD and created/updated/cancelled as
       "YYYY-MM-DD HH:MM:SS", neither of which the DateTime scalar accepts, so each is
       normalised to RFC 3339 here. Every other field passes through unchanged under its
       snake_case Ordergroove name.

       No derived display labels (status, cadence, quantity, prepaid) are built here, and
       none should be added: type Subscription in actions/actions_schema.graphql declares
       only the snake_case fields plus the structured prepaid_subscription_context, so the
       GraphQL layer discards any extra key this template emits. Display labels are owned
       solely by the subscriptions data pull, whose schema declares them and whose card
       actually reads them. */ -}}
{{- $s := .rawData -}}
{{- if $s.start_date -}}{{- $_ := set $s "start_date" ($s.start_date | toDate "2006-01-02" | date "2006-01-02T15:04:05Z") -}}{{- end -}}
{{- if and $s.cancelled (ne (printf "%v" $s.cancelled) "null") -}}{{- $_ := set $s "cancelled" ($s.cancelled | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}{{- end -}}
{{- if $s.created -}}{{- $_ := set $s "created" ($s.created | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}{{- end -}}
{{- if $s.updated -}}{{- $_ := set $s "updated" ($s.updated | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}{{- end -}}
{{toJson $s}}
