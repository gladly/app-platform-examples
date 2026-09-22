{{- /* The picker carries "<orderId>|<subscriptionId>" so the two can never be mismatched.
       Form attrs arrive at the TOP LEVEL of the template data - `.orderRef`, not
       `.form.orderRef`; there is no `.form` root. Unpacking is guarded so a malformed or
       empty selection yields empty ids and lets the action's own request_url guard emit a
       proper stop message, rather than crashing the template on an out-of-range index. */ -}}
{{- $ref := printf "%v" (default "" .orderRef) -}}
{{- $parts := splitList "|" $ref -}}
{{- $orderId := "" -}}
{{- $subscriptionId := "" -}}
{{- if ge (len $parts) 2 -}}
  {{- $orderId = index $parts 0 -}}
  {{- $subscriptionId = index $parts 1 -}}
{{- end -}}
{
  "orderId": {{ $orderId | toJson }},
  "subscriptionId": {{ $subscriptionId | toJson }}
}
