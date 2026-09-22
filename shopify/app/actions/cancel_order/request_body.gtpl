{{ $query := printf `
mutation orderCancel($notifyCustomer: Boolean, $orderId: ID!, $reason: OrderCancelReason!, $refundMethod: OrderCancelRefundMethodInput, $restock: Boolean!, $staffNote: String) {
    orderCancel(
        notifyCustomer: $notifyCustomer,
        orderId: $orderId,
        reason: $reason,
        refundMethod: $refundMethod,
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