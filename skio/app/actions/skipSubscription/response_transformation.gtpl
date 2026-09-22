{{- /* Gladly AI and Team Assist call this action directly and never render an agent form,
       so the translation of Skio's misleading errors lives here, in the action, and every
       caller gets it. `errorMessage` is the translated, agent-facing text; `errors` keeps
       Skio's untranslated original.

       Skio rejects a write two ways: a top-level `errors` array, and a 200 whose mutation
       payload carries `ok: false` with a `message`. Both are failures, and both set
       `ok: false` and `errorMessage`. */ -}}
{{- $errs := .rawData.errors -}}
{{- if and (ne $errs nil) (gt (len $errs) 0) -}}
    {{- $parts := list -}}
    {{- range $e := $errs -}}
        {{- /* extensions.detail beats message: Skio's `message` is often a useless
               "Unauthorized". `with`, not kindIs - an absent key panics on some paths. */ -}}
        {{- $msg := $e.message | default "Skio rejected the change." | toString -}}
        {{- with $e.extensions -}}
            {{- $d := .detail | default "" | toString -}}
            {{- if ne $d "" -}}{{- $msg = $d -}}{{- end -}}
        {{- end -}}
        {{- /* Four Skio errors send the caller to the wrong action. Rewrite exactly these and
               pass everything else through verbatim - a wrapper that rewords every error hides
               the useful ones. See "Error translation" in the app README. */ -}}
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
{
  "ok": false,
  "errorMessage": {{ join "; " (uniq $parts) | toJson }},
  "errors": {{ toJson $errs }}
}
{{- else if .rawData.data.skipSubscription -}}
{{- $payload := .rawData.data.skipSubscription -}}
{{- if eq (printf "%v" $payload.ok) "false" -}}
    {{- $detail := $payload.message | default "Skio rejected the change." | toString -}}
    {{- /* Four Skio errors send the caller to the wrong action. Rewrite exactly these and
           pass everything else through verbatim - a wrapper that rewords every error hides
           the useful ones. See "Error translation" in the app README. */ -}}
    {{- if contains "Lambda policy revoked" $detail -}}
        {{- $detail = "Skio has no such subscription on this store. Check the id - this is not a permissions problem." -}}
    {{- else if eq (lower (trim $detail)) "internal error" -}}
        {{- $detail = "Skio could not read that id. Check it is a complete subscription id copied from the Skio card." -}}
    {{- else if eq (upper (trim $detail)) "NOT_FOUND" -}}
        {{- $detail = "Skio could not find that. If this was a discount code, the merchant's allowlist has it but Skio does not - it may have been deleted or expired in Skio." -}}
    {{- else if eq (upper (trim $detail)) "CURRENTLY_INACTIVE" -}}
        {{- $detail = "That discount code exists but is not active right now - its start date has not arrived, or it has already ended. Nothing is wrong with the subscription. The merchant can change the schedule in Shopify." -}}
    {{- end -}}
{{- toJson (merge (dict "ok" false "errorMessage" $detail) $payload) -}}
{{- else -}}
{{- toJson $payload -}}
{{- end -}}
{{- else -}}
{{- fail "Unexpected response structure from Skio API for skipSubscription" -}}
{{- end -}}
