{{ $query := printf `
mutation orderCancel($notifyCustomer: Boolean, $orderId: ID!, $reason: OrderCancelReason!, $refund: Boolean!, $restock: Boolean!, $staffNote: String) {
    orderCancel(
        notifyCustomer: $notifyCustomer, 
        orderId: $orderId, 
        reason: $reason, 
        refund: $refund, 
        restock: $restock, 
        staffNote: $staffNote
    ) {
        job {
            id
            done
        }
        orderCancelUserErrors {
            code
            field
            message
        }
    }
}
` }}

{
    "query": {{toJson $query}},
    "variables": {{ toJson .inputs}}
}