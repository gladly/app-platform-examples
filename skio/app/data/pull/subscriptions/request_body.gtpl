{{- /* Look up subscriptions for a storefront user */ -}}
{{$externalUser := index .externalData.skio_storefront_user 0}}
{{$query := printf `
query {
    Subscriptions(
        where: {
            StorefrontUser: {id: {_eq: "%s"}}
        },
        order_by: {createdAt: desc}
    ) {
        id
        platformId
        status
        createdAt
        nextBillingDate
        storefrontUserId
        cyclesCompleted
        cancelledAt
        currencyCode
        BillingPolicy {
            id
            interval
            intervalCount
            isMaxCycleV2
            maxCycles
            minCycles
            createdAt
            updatedAt
        }
        DeliveryPolicy {
            id
            interval
            intervalCount
            isMaxCycleV2
            maxCycles
            minCycles
            createdAt
            updatedAt
        }
        SubscriptionLines(where: {removedAt: {_is_null: true}}) {
            id
            priceWithoutDiscount
            quantity
            sellingPlanId
            isPrepaid
            ordersRemaining
            ProductVariant {
                title
                Product {
                    title
                }
            }
        }
    }
}
` $externalUser.id }}

{
    "query": {{toJson $query}}
} 