{{- /* The picker carries "<kind>|<id>|<customerId>|<merchantId>" in one value. There is
       no hidden section type in an App Platform form (validate rejects it), and the
       agent must not be able to retype an identity field, so the identifiers ride along
       with the target choice and are split back out here. Packing also makes it
       impossible to pick both an item and an order, which the action refuses.
       Form attrs arrive at the TOP LEVEL of the template data - `.target`, not
       `.form.target`; there is no `.form` root. */ -}}
{{- $target := printf "%v" (default "" .target) -}}
{{- $parts := splitList "|" $target -}}
{{- $kind := "" -}}{{- $id := "" -}}{{- $customerId := "" -}}{{- $merchantId := "" -}}
{{- if ge (len $parts) 4 -}}
  {{- $kind = index $parts 0 -}}
  {{- $id = index $parts 1 -}}
  {{- $customerId = index $parts 2 -}}
  {{- $merchantId = index $parts 3 -}}
{{- end -}}
{
  "merchantId": {{ $merchantId | toJson }},
  "customerId": {{ $customerId | toJson }},
  {{- if eq $kind "item" }}
  "itemId": {{ $id | toJson }},
  {{- end }}
  {{- if eq $kind "order" }}
  "orderId": {{ $id | toJson }},
  {{- end }}
  "discountType": {{ (default "" .discountType) | toJson }},
  "value": {{ (default "" .value) | toJson }},
  {{- with .reason }}
  "reason": {{ . | toJson }},
  {{- end }}
  "confirmed": {{ if .confirmed }}true{{ else }}false{{ end }}
}
