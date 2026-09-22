{{- /* A Skio-side failure arrives on an HTTP 200 as {"errors": [...], "data": null}. Folding
       that into the not-found branch presented it to agents as an absent customer with an empty
       card, and blamed a missing profile in the logs instead of the query that actually broke,
       so it is checked first and fails loudly with Skio's own text. `with` is the only nil-safe
       way in: `errors` is absent on every successful response, and a key the payload omits is an
       invalid reflect.Value that panics both `len` and sprig's `default`. */ -}}
{{- $skioErrors := list -}}
{{- with .rawData.errors -}}
    {{- range $e := . -}}
        {{- $m := "" -}}
        {{- if kindIs "map" $e -}}
            {{- with $e.message -}}{{- $m = . | toString -}}{{- end -}}
        {{- end -}}
        {{- if eq $m "" -}}{{- $m = toJson $e -}}{{- end -}}
        {{- $skioErrors = append $skioErrors $m -}}
    {{- end -}}
{{- end -}}
{{- if gt (len $skioErrors) 0 -}}
    {{- fail (printf "Skio returned an error for the StorefrontUsers query: %s" (join "; " $skioErrors)) -}}
{{- end -}}
{{- /* Exactly one match or nothing: an ambiguous or absent customer must fail closed rather
       than render someone else's subscriptions. */ -}}
{{- if or (not .rawData.data) (not .rawData.data.StorefrontUsers) (eq (len .rawData.data.StorefrontUsers) 0) -}}
    {{- stop "StorefrontUser does not exist" -}}
{{- else if gt (len .rawData.data.StorefrontUsers) 1 -}}
    {{- stop "Skio returned more than one user for the customer profile email." -}}
{{- else -}}
    {{- $user := index .rawData.data.StorefrontUsers 0 -}}
    {{- /* Always-present scalars first. A null nested field blanks the WHOLE card, so optional
           objects are only set when they are real. */ -}}
    {{- $out := dict
        "id" $user.id
        "email" (default "" $user.email)
        "platformId" (default "" $user.platformId)
        "createdAt" (default "" $user.createdAt)
        "updatedAt" (default "" $user.updatedAt)
    -}}
    {{- range $field := list "firstName" "lastName" "phoneNumber" "redactedAt" -}}
        {{- $value := get $user $field -}}
        {{- if and (ne $value nil) (ne ($value | toString) "") -}}
            {{- $out = set $out $field ($value | toString) -}}
        {{- end -}}
    {{- end -}}
    {{- if ne $user.smsTransactionalOptIn nil -}}
        {{- $out = set $out "smsTransactionalOptIn" $user.smsTransactionalOptIn -}}
    {{- end -}}

    {{- /* Addresses on file. platformId is what points a subscription at one of these. */ -}}
    {{- $addresses := list -}}
    {{- range $a := (default list $user.ShippingAddresses) -}}
        {{- $addresses = append $addresses $a -}}
    {{- end -}}
    {{- if gt (len $addresses) 0 -}}{{- $out = set $out "ShippingAddresses" $addresses -}}{{- end -}}

    {{- toJson $out -}}
{{- end -}}
