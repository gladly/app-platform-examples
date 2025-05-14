{{if .rawData.errors}}
{
  "errors": {{ toJson .rawData.errors }}
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
fail "Unexpected response structure from Skio API for getSubscriptionById"
{{end}}