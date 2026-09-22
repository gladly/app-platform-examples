{{- /* Gladly AI and Team Assist call this action directly and never render an agent form,
       so the translation of Skio's misleading errors lives here, in the action, and every
       caller gets it. `errorMessage` is the translated, agent-facing text; `errors` keeps
       Skio's untranslated original. A read has no ok flag to falsify - the errors array is
       Skio's only failure shape here. */ -}}
{{- $errs := .rawData.errors -}}
{{- if and (ne $errs nil) (gt (len $errs) 0) -}}
    {{- $parts := list -}}
    {{- range $e := $errs -}}
        {{- /* extensions.detail beats message: Skio's `message` is often a useless
               "Unauthorized". `with`, not kindIs - an absent key panics on some paths. */ -}}
        {{- $msg := $e.message | default "Skio rejected the request." | toString -}}
        {{- with $e.extensions -}}
            {{- $d := .detail | default "" | toString -}}
            {{- if ne $d "" -}}{{- $msg = $d -}}{{- end -}}
        {{- end -}}
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
{
  "errorMessage": {{ join "; " (uniq $parts) | toJson }},
  "errors": {{ toJson $errs }}
}
{{/* Handle cases where SubscriptionByPk might be null (e.g., not found) but not an API error */}}
{{else if .rawData.data }}
  {{ $sub := .rawData.data.SubscriptionByPk }}
  {
    {{/* Only include subscription if it's not null */}}
    {{ if $sub }}
    "subscription": {
      "id": {{ toJson $sub.id }},
      "status": {{ toJson $sub.status }},
      "createdAt": {{ toJson $sub.createdAt }},
      "nextBillingDate": {{ toJson $sub.nextBillingDate }},
      "storefrontUserId": {{ toJson $sub.storefrontUserId }},
      "BillingPolicy": {
        "id": {{ toJson $sub.BillingPolicy.id }},
        "interval": {{ toJson $sub.BillingPolicy.interval }},
        "intervalCount": {{ toJson $sub.BillingPolicy.intervalCount }},
        "isMaxCycleV2": {{ toJson $sub.BillingPolicy.isMaxCycleV2 }},
        "maxCycles": {{ toJson $sub.BillingPolicy.maxCycles }},
        "minCycles": {{ toJson $sub.BillingPolicy.minCycles }},
        "createdAt": {{ toJson $sub.BillingPolicy.createdAt }},
        "updatedAt": {{ toJson $sub.BillingPolicy.updatedAt }}
      },
      "DeliveryPolicy": {
        "id": {{ toJson $sub.DeliveryPolicy.id }},
        "interval": {{ toJson $sub.DeliveryPolicy.interval }},
        "intervalCount": {{ toJson $sub.DeliveryPolicy.intervalCount }},
        "isMaxCycleV2": {{ toJson $sub.DeliveryPolicy.isMaxCycleV2 }},
        "maxCycles": {{ toJson $sub.DeliveryPolicy.maxCycles }},
        "minCycles": {{ toJson $sub.DeliveryPolicy.minCycles }},
        "createdAt": {{ toJson $sub.DeliveryPolicy.createdAt }},
        "updatedAt": {{ toJson $sub.DeliveryPolicy.updatedAt }}
      },
      "SubscriptionLines": [
        {{ range $index, $line := $sub.SubscriptionLines }}
          {{ if $index }},{{ end }}
          {
            "id": {{ toJson $line.id }},
            "priceWithoutDiscount": {{ toJson $line.priceWithoutDiscount }},
            "quantity": {{ toJson $line.quantity }},
            "isPrepaid": {{ toJson $line.isPrepaid }},
            "ordersRemaining": {{ toJson $line.ordersRemaining }},
            "sellingPlanId": {{ toJson $line.sellingPlanId }},
            "ProductVariant": {
              "title": {{ toJson $line.ProductVariant.title }},
              "Product": {
                "title": {{ toJson $line.ProductVariant.Product.title }}
              }
            }
          }
        {{ end }}
      ],
      "platformId": {{ toJson $sub.platformId }},
      "currencyCode": {{ toJson $sub.currencyCode }},
      "cyclesCompleted": {{ toJson $sub.cyclesCompleted }},
      "cancelledAt": {{ toJson $sub.cancelledAt }},
      "updatedAt": {{ toJson $sub.updatedAt }}
    }
    {{ else }}
    "subscription": null
    {{ end }}
  }
{{/* Fallback for unexpected response structures */}}
{{else}}
{{- fail "Unexpected response structure from Skio API for getSubscriptionById" -}}
{{end}}