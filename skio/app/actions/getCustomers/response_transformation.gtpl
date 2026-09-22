{{- /* This action returns a LIST, so there is nowhere to put an error envelope: an
       {"errors": ...} object against a list-typed field is a type failure rather than a
       readable error. Fail loudly instead, and return an empty list ONLY when Skio really
       answered with no match.

       Two shapes are failure, and neither may fall through to an empty list - an agent who
       reads "no results" during a Skio outage concludes the customer has no Skio account:
         1. a top-level `errors` array;
         2. any response that does not carry a StorefrontUsers LIST at all - an HTTP 500 or 429
            whose body has no `errors` array, a `"data": null`, a null StorefrontUsers.

       `with` is the only nil-safe way to test `.rawData.errors`: `errors` is absent on every
       successful response, and a key the payload omits is an invalid reflect.Value that panics
       both `len` and sprig's `default`. Same reason `.rawData.data` is reached through `with`
       and the list through `get` rather than a direct field walk.

       The failure text is translated the same way every other action's errorMessage is:
       Gladly AI and Team Assist call this directly and never render a form, so an
       untranslated "Lambda policy revoked" would send the caller after a permissions
       problem that does not exist. */ -}}
{{- $parts := list -}}
{{- with .rawData.errors -}}
    {{- range $e := . -}}
        {{- /* extensions.detail beats message: Skio's `message` is often a useless
               "Unauthorized". `with`, not a direct read - an absent key panics on some paths. */ -}}
        {{- $msg := "" -}}
        {{- if kindIs "map" $e -}}
            {{- with $e.message -}}{{- $msg = . | toString -}}{{- end -}}
            {{- with $e.extensions -}}
                {{- $d := "" -}}
                {{- with .detail -}}{{- $d = . | toString -}}{{- end -}}
                {{- if ne $d "" -}}{{- $msg = $d -}}{{- end -}}
            {{- end -}}
        {{- end -}}
        {{- if eq $msg "" -}}{{- $msg = toJson $e -}}{{- end -}}
        {{- /* Four Skio errors send the caller to the wrong action. Rewrite exactly these and
               pass everything else through verbatim. See "Error translation" in the app README. */ -}}
        {{- if contains "Lambda policy revoked" $msg -}}
            {{- $msg = "Skio has no such subscription on this store. Check the id - this is not a permissions problem." -}}
        {{- else if eq (lower (trim $msg)) "internal error" -}}
            {{- $msg = "Skio could not read that id. Check it is a complete subscription id copied from the Skio card." -}}
        {{- else if eq (upper (trim $msg)) "NOT_FOUND" -}}
            {{- $msg = "Skio could not find that. If this was a discount code, the merchant's allowlist has it but Skio does not - it may have been deleted or expired in Skio." -}}
        {{- else if eq (upper (trim $msg)) "CURRENTLY_INACTIVE" -}}
            {{- $msg = "That discount code exists but is not active right now - its start date has not arrived, or it has already ended. Nothing is wrong with the subscription. The merchant can change the schedule in Shopify." -}}
        {{- end -}}
        {{- $parts = append $parts $msg -}}
    {{- end -}}
{{- end -}}
{{- if gt (len $parts) 0 -}}
    {{- fail (printf "Skio returned an error looking up customers: %s" (join "; " (uniq $parts))) -}}
{{- end -}}
{{- $data := dict -}}
{{- with .rawData.data -}}
    {{- if kindIs "map" . -}}{{- $data = . -}}{{- end -}}
{{- end -}}
{{- $results := get $data "StorefrontUsers" -}}
{{- if not (kindIs "slice" $results) -}}
    {{- fail (printf "Skio returned a response the Skio app could not read looking up customers: no StorefrontUsers list in the payload, and no errors array explaining why. Skio's raw response was: %s" (trunc 500 (toJson .rawData))) -}}
{{- end -}}
{{- toJson $results -}}
