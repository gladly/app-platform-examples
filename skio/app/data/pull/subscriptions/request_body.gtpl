{{- /* Every subscription for the storefront user resolved by the parent pull.

       The interpolated id goes through `toJson`, which supplies the string literal's quotes and
       escapes anything inside it - the same treatment every value in the parent pull's
       where-clause gets, and the reason there are no quotes in the format string. The id comes
       from Skio rather than from a profile, but there is no reason for one pull to build its
       filter safely and the other not to.

       Not a GraphQL variable: this is a uuid column, which accepts an inline string literal but
       rejects a `String!` variable on type grounds, and the Hasura input type name it would
       need instead cannot be verified offline.

       Both list selections are bounded. The shipped version asked for Subscriptions and
       SubscriptionLines with no limit at all, against Skio's 100-node-per-response cap - fine
       for a two-product subscriber, a silent failure for a power subscriber.

       StorefrontUser -> Subscriptions -> SubscriptionLines -> ProductVariant -> Product is
       already at Skio's depth-4 ceiling, so nothing here can nest deeper. Reads that would need
       a fifth level (cancel-flow sessions, credit ledger, payment health) live in the separate
       `retention` pull instead. */ -}}
{{- $externalUser := index .externalData.skio_storefront_user 0 -}}
{{- $query := printf `
query {
    Subscriptions(
        where: {
            StorefrontUser: {id: {_eq: %s}}
        },
        order_by: {createdAt: desc},
        limit: 10
    ) {
        id
        platformId
        status
        statusContext
        createdAt
        updatedAt
        nextBillingDate
        cancelledAt
        storefrontUserId
        cyclesCompleted
        currencyCode
        note
        customAttributes
        churnScore
        churnRiskReasons
        churnPredictedAt
        deliveryPrice
        deliveryPriceOverride
        isPickup
        remainingCyclesUntilRenewal
        nextRenewalDate
        lastBillingAttemptAt
        streakCount
        maxStreakCount
        streakExpiresAt
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
        PrepaidDeliveryPolicy {
            id
            interval
            intervalCount
            isMaxCycleV2
            maxCycles
            minCycles
            createdAt
            updatedAt
        }
        ShippingAddress {
                id
                platformId
                firstName
                lastName
                company
                address1
                address2
                city
                province
                country
                zip
                phoneNumber
                doorCode
        }
        Discounts(limit: 3) {
            id
            redeemCode
            title
            type
            percentage
            fixedValue
            timesUsed
            maxTimesUsed
            appliesOnEachItem
        }
        SubscriptionLines(where: {removedAt: {_is_null: true}}, limit: 10) {
            id
            Discounts(limit: 3) {
                id
                redeemCode
                title
                type
                percentage
                fixedValue
                timesUsed
                maxTimesUsed
            }
            platformId
            priceWithoutDiscount
            quantity
            sellingPlanId
            isPrepaid
            ordersRemaining
            productVariantId
            titleOverride
            customAttributes
            ProductVariant {
                id
                title
                platformId
                sku
                price
                compareAtPrice
                outOfStockAt
                Product {
                    title
                }
            }
        }
    }
}
` (toJson ($externalUser.id | toString)) -}}
{
  "query": {{ toJson $query }}
}
