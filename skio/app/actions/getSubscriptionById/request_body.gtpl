{
  "query": "query getSubscriptionById($id: uuid!) { SubscriptionByPk(id: $id) { id platformId storefrontUserId status currencyCode cyclesCompleted createdAt updatedAt cancelledAt nextBillingDate BillingPolicy { id interval intervalCount isMaxCycleV2 maxCycles minCycles createdAt updatedAt } DeliveryPolicy { id interval intervalCount isMaxCycleV2 maxCycles minCycles createdAt updatedAt } SubscriptionLines(where: {removedAt: {_is_null: true}}) { id priceWithoutDiscount quantity isPrepaid ordersRemaining sellingPlanId ProductVariant { title Product { title } } } } }",
  "variables": {
    "id": "{{.inputs.subscriptionId}}"
  }
} 