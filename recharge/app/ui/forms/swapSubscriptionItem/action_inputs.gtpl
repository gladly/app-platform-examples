{{- /* subscriptionId + externalVariantId are required; quantity (Int) is optional -- omit it when
       the agent left it blank. quantity is coerced to an int. No price field: the price-override
       bypass around the guarded adjustSubscriptionPrice cap was closed by dropping it (v1). */ -}}
{{- $out := dict "subscriptionId" .subscriptionId "externalVariantId" .externalVariantId -}}
{{- if and (ne .quantity nil) (ne (toString .quantity) "")}}{{- $out = set $out "quantity" (.quantity | int)}}{{- end -}}
{{ toJson $out }}
