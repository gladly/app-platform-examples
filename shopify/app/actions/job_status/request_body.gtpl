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
            note
            totalRefundedSet {
              shopMoney {
                amount
                currencyCode
              }
            }
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