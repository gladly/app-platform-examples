{{- /* SECURITY: the gid is interpolated into the JSON variables payload below. Even
       though it travels as a GraphQL *variable* (not into the query AST, so GraphQL
       injection isn't possible), a stray quote/backslash could break the JSON — so we
       require a well-formed Shopify Order GID. This also gives a clean error for the
       common mistake of passing an order number instead of a GID. */ -}}
{{ if or (eq .inputs.gid nil) (eq .inputs.gid "") }}
	{{ stop "Input gid is required (a 'gid://shopify/Order/...' order ID)." }}
{{ end }}
{{ if not (regexMatch `^gid://shopify/Order/[0-9]+$` .inputs.gid) }}
	{{ stop "gid must be a Shopify Order GID like 'gid://shopify/Order/1234567890'. This action does not accept order numbers." }}
{{ end }}

{{- /* Shopify resolves a GID via orderByIdentifier(identifier:{ id }). Line items come
       back as ONE flat list; the sibling blocks (fulfillments / fulfillmentOrders /
       returns / refunds) key back to lines by lineItem.id. NOTE the connection-vs-list
       split confirmed on the live API: fulfillments and refunds are PLAIN LISTS, while
       lineItems / fulfillmentOrders / returns are connections (.nodes). */ -}}
{{- $query := `
query OrderByIdentifier($id: ID!) {
  orderByIdentifier(identifier: { id: $id }) {
    id
    name
    confirmationNumber
    displayFinancialStatus
    displayFulfillmentStatus
    createdAt
    processedAt
    email
    note
    tags
    cancelReason
    cancelledAt
    originalTotalPriceSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
    originalTotalDutiesSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
    subtotalPriceSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
    totalShippingPriceSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
    totalTaxSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
    totalPriceSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
    currentSubtotalPriceSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
    currentTotalDiscountsSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
    currentCartDiscountAmountSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
    currentShippingPriceSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
    currentTotalTaxSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
    currentTotalDutiesSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
    currentTotalPriceSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
    totalReceivedSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
    totalRefundedSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
    totalRefundedShippingSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
    totalOutstandingSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
    refundDiscrepancySet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
    totalCapturableSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
    shippingAddress { address1 address2 city company country countryCodeV2 firstName lastName name phone province provinceCode zip formatted }
    billingAddress { address1 address2 city company country countryCodeV2 firstName lastName name phone province provinceCode zip formatted }
    channelInformation { channelDefinition { channelName } }
    app { name }
    shippingLines(first: 5) { nodes { title } }
    purchasingEntity {
      __typename
      ... on Customer {
        id
        displayName
        defaultEmailAddress { emailAddress }
        defaultPhoneNumber { phoneNumber }
        defaultAddress { formatted(withCompany: true) }
      }
      ... on PurchasingCompany {
        company { id name }
        contact { id customer { id displayName } }
        location { id name }
      }
    }
    lineItems(first: 50) {
      pageInfo { hasNextPage }
      nodes {
        id
        name
        title
        sku
        vendor
        isGiftCard
        currentQuantity
        quantity
        unfulfilledQuantity
        customAttributes { key value }
        originalUnitPriceSet { shopMoney { amount currencyCode } }
        totalDiscountSet { shopMoney { amount currencyCode } }
        variant { id title selectedOptions { name value } }
        product {
          id
          title
          vendor
          productType
          collections(first: 50) {
            nodes { id title }
            pageInfo { hasNextPage }
          }
        }
      }
    }
    fulfillments {
      id
      name
      displayStatus
      deliveredAt
      estimatedDeliveryAt
      trackingInfo { company number url }
      fulfillmentLineItems(first: 50) { nodes { quantity lineItem { id } } }
    }
    fulfillmentOrders(first: 20) {
      pageInfo { hasNextPage }
      nodes {
        id
        status
        assignedLocation { name }
        fulfillmentHolds { reason reasonNotes }
        lineItems(first: 50) { nodes { remainingQuantity lineItem { id } } }
        deliveryMethod { methodType minDeliveryDateTime maxDeliveryDateTime }
      }
    }
    returns(first: 20) {
      pageInfo { hasNextPage }
      nodes {
        id
        name
        status
        totalQuantity
        returnLineItems(first: 50) {
          nodes {
            quantity
            ... on ReturnLineItem {
              returnReasonNote
              returnReasonDefinition { name }
              fulfillmentLineItem { lineItem { id } }
            }
          }
        }
      }
    }
    refunds {
      id
      createdAt
      note
      totalRefundedSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
      refundLineItems(first: 50) { nodes { quantity subtotalSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } } totalTaxSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } } restockType lineItem { id } } }
      transactions(first: 50) { nodes { id kind status gateway formattedGateway accountNumber amountSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } } } }
    }
    additionalFees {
      id
      name
      price { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } }
    }
  }
}
` -}}

{
    "query": {{ toJson $query }},
    "variables": {
        "id": "{{ .inputs.gid }}"
    }
}