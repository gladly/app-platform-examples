{{ $query := `
query jobStatus($jobId: ID!, $orderId: ID!) {
  job(id: $jobId) {
    id
    done
    query {
      order(id: $orderId) {
          name
          cancelledAt
          cancelReason
          refunds(first: 10) {
            id
            createdAt
            note
            totalRefundedSet {
              shopMoney { amount currencyCode }
              presentmentMoney { amount currencyCode }
            }
            refundLineItems(first: 50) { nodes { quantity restockType lineItem { id } } }
            transactions(first: 10) { nodes { id kind status gateway formattedGateway accountNumber amountSet { shopMoney { amount currencyCode } presentmentMoney { amount currencyCode } } } }
          }
        }
    }
  }
}
` }}

{
    "query": {{- toJson $query -}},
    "variables": {{- toJson .inputs -}}
}