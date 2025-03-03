# Shopify App Overview

## Benefits

Used with Gladly Sidekick, the Shopify app empowers your customers to resolve issues related to their orders, e.g. find their most recent order, check their shipping address or status of their shipments, etc

## Features

It can be used in Sidekick to:

1. Retrieve customer and order data from Shopify (e.g. shipping address, most recent orders, etc)
2. Lookup Shopify orders by order number and email. This includes shipping/tracking information where available.

# Shopify App Toolkit

## Who maintains the integration

The Shopify integration is built and maintained by Gladly.

## Basic scope

This app provides methods of reading Customer and Order data from Shopify. These methods can then be used in Sidekick to directly answer customer questions. This app itself does not display any data nor does it provide any customer interface.

## How the integration works

The Shopify App integrates with the Shopify platform via [Admin GraphQL](https://shopify.dev/docs/api/admin-graphql)
To verify which version of the API is used check actions/data pulls' individual `request_url.gtpl` files, e.g. for orders: https://github.com/sagansystems/ps-app-platform/blob/ab9346091c1b88a493065a7ad997a1f83f00277b/shopify/data/pull/orders/request_url.gtpl#L2

## Configuration

We are using the following defaults:

- Where appropriate data (customers, orders) is returned in the following order `reverse: true, sortKey: UPDATED_AT`. That is most recently updated orders are returned first
- By default we return 40 newest orders (as described above). You can change this in integration `config` by changing `ordersLimit`, e.g.

  ```
  {
    "ordersLimit": 10
  }
  ```

## Important

This app can be used to retrieve any order from a Shopify instance with the associated email address or order number. For security and privacy reasons, you may want to verify a Customer's identity by asking for their zipcode or phone number and validating within Sidekick before displaying any results or allowing the Customer to make changes to an order.

# Implementation Details

## Data pull: Customer and Order retrieval

**Data**

Customer matching:
https://github.com/sagansystems/ps-app-platform/blob/3d9e697fde2dfda971de2b181b99370d042c0e74/shopify/data/pull/customer/request_body.gtpl#L1-L4

Shopify `Customer` documentation and all available fields (of which some will not be available in Gladly by default), can be found [here](https://shopify.dev/docs/api/admin-graphql/unstable/objects/Customer). Ensure you are viewing the documentation for correct API version.

Order matching:
https://github.com/sagansystems/ps-app-platform/blob/43f14258ca423c75ea927af11674fc4860e6c540/shopify/data/pull/orders/request_body.gtpl#L1-L4

Shopify `Order` documentation and all available fields (of which some will not be available in Gladly by default), can be found [here](https://shopify.dev/docs/api/admin-graphql/unstable/objects/order)

<details>
<summary>Data available by default in Gladly</summary>
https://github.com/sagansystems/ps-app-platform/blob/ab9346091c1b88a493065a7ad997a1f83f00277b/shopify/data/data_schema.graphql#L1-L306
</details>

**Errors**

Since this is Data Pull it's assumed there is no error client and if any Shopify errors are encountered they will not be returned to the client. Hence all errors should be ignored.

https://github.com/sagansystems/ps-app-platform/blob/0dc699f40613626b7281dfb15884a679781ac697/shopify/data/pull/customer/response_transformation.gtpl#L1

## Actions

Available Query actions:
https://github.com/sagansystems/ps-app-platform/blob/0091e6a77d10518bc88de7a780e8b1796946c170/shopify/actions/actions_schema.graphql#L445-L456

### Error handling

Since we are using GraphQL most queries to Shopify will result in 200 responses. Any errors which would typically produce 4xx or 5xx errors in REST, will be returned in action Result as `errors`. They have the following format: https://github.com/sagansystems/ps-app-platform/blob/ab9346091c1b88a493065a7ad997a1f83f00277b/shopify/actions/actions_schema.graphql#L323-L326

Any non-200 responses received from Shopify will be returned to client as 500.

You can read more on Shopify Errors in [Shopify documentation](https://shopify.dev/docs/api/admin-graphql#status_and_error_codes)

### Lookup Order by Order Number

Definition and input:
https://github.com/sagansystems/ps-app-platform/blob/c3409dcc8dfb7d7b3b355bbdd8c2a41b9f39dbe3/shopify/actions/actions_schema.graphql#L446-L449

Response:
https://github.com/sagansystems/ps-app-platform/blob/ab9346091c1b88a493065a7ad997a1f83f00277b/shopify/actions/actions_schema.graphql#L329-L333

Example success response: https://github.com/sagansystems/ps-app-platform/blob/master/shopify/actions/look_up_order_by_order_num/_test_/data/success/expected_response_transformation.json

### Lookup Orders by Email Address

Definition and input:
https://github.com/sagansystems/ps-app-platform/blob/21893f56552db091282148e8778be30544314311/shopify/actions/actions_schema.graphql#L450-L451

Response:
https://github.com/sagansystems/ps-app-platform/blob/21893f56552db091282148e8778be30544314311/shopify/actions/actions_schema.graphql#L347-L351

https://github.com/sagansystems/ps-app-platform/blob/d3780137b48c500fd2005f1acc99f001b90f0769/shopify/actions/look_up_orders_by_email/request_body.gtpl#L1-L6

Example success response: https://github.com/sagansystems/ps-app-platform/blob/master/shopify/actions/look_up_orders_by_email/_test_/data/success/expected_response_transformation.json

### Refund Line Item

TODO: change urls after merge

Definition and input:
https://github.com/sagansystems/ps-app-platform/pull/40/files#diff-dc09b6eb0e5d89d15552accd01f87791cd6ef2e299071f4e2dc15a5f1bfcc675R707-R723

Response:
https://github.com/sagansystems/ps-app-platform/pull/40/files#diff-dc09b6eb0e5d89d15552accd01f87791cd6ef2e299071f4e2dc15a5f1bfcc675R539-R549

https://github.com/sagansystems/ps-app-platform/blob/e36cc8c2ce9ba8cac5b76a3a4d33d08dfd2099c4/shopify/actions/refund_line_item/request_body.gtpl

https://github.com/sagansystems/ps-app-platform/blob/e36cc8c2ce9ba8cac5b76a3a4d33d08dfd2099c4/shopify/actions/refund_line_item/_test_/data/success/expected_response_transformation.json

Example success response: 
https://github.com/sagansystems/ps-app-platform/blob/e36cc8c2ce9ba8cac5b76a3a4d33d08dfd2099c4/shopify/actions/refund_line_item/_test_/data/success/expected_response_transformation.json

### Cancel Order
TBD

### Job Status
TBD

### Suggested Refund
TBD

### Update Shipping Address
TBD 

## Scenarios

Below are example workflows to illustrate how above actions and data pulls can be used.

### Customer looking for a missing order. They have an Order Number.

** Via Data Pull (preferred) **

1. Ask for email address. This will allow you to run a Data Pull and hence persist customer's Shopify data to be used in other parts of Gladly.
2. Look through returned orders and check if any of them match the Order Number. If not proceed to actions

** Via Lookup Order By Order Number **

1. Perform `lookup_order_by_order_num`
2. Ask customer to verify part of the address for security.

### Cancel order
TBD

### Refund line item
TBD

### Update shipping address
TBD
