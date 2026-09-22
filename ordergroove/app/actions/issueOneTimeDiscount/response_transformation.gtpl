{{- /* Ordergroove returns 201 Created on success, NOT 200. */ -}}
{{- if and .response (ne .response.statusCode 201) (ne .response.statusCode 200) -}}
  {{- if eq .response.statusCode 400 -}}
    {{- if .rawData.detail -}}
      {{ stop (printf "Issue discount: %s" .rawData.detail) }}
    {{- end -}}
    {{- range $field, $msgs := .rawData -}}
      {{- if kindIs "slice" $msgs -}}
        {{- if gt (len $msgs) 0 -}}
          {{ stop (printf "Issue discount: %s" (join ", " $msgs)) }}
        {{- end -}}
      {{- end -}}
    {{- end -}}
    {{ stop "Issue discount: Ordergroove rejected the discount." }}
  {{- end -}}
  {{- if eq .response.statusCode 404 -}}
    {{ stop "Issue discount: the customer, item or order could not be found in Ordergroove." }}
  {{- end -}}
  {{ fail (printf "Issue discount failed: %d %s" .response.statusCode .response.body) }}
{{- end -}}
{{- $r := .rawData -}}
{{- if $r.created -}}{{- $_ := set $r "created" ($r.created | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}{{- end -}}
{{- if $r.last_updated -}}{{- $_ := set $r "last_updated" ($r.last_updated | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}{{- end -}}
{{- $_ := set $r "publicId" $r.public_id -}}
{{- $_ := set $r "externalCode" $r.external_code -}}
{{- $_ := set $r "lastUpdated" $r.last_updated -}}
{{- /* Flatten the nested incentive so the card and result never bind into a nullable
       nested object - a null nested field blanks the entire card. */ -}}
{{- $inc := $r.incentive -}}
{{- if kindIs "map" $inc -}}
  {{- $_ := set $r "incentiveName" (get $inc "name") -}}
  {{- $_ := set $r "discountType" (get $inc "discount_type") -}}
  {{- $_ := set $r "discountValue" (printf "%v" (get $inc "value")) -}}
  {{- $_ := set $r "discountTarget" (get $inc "target") -}}
{{- end -}}
{{- $_ := set $r "message" "Discount issued. It applies to the order when it places - refresh the card to confirm the new total." -}}
{{toJson $r}}
