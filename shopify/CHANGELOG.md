# version "3.1.0"

- Added new field `orderNumber` to the `Order` type in all relevant schemas and response transformations.
- Updated response transformation templates for order lookups and data pulls to include `orderNumber`.
- Bumped app version to 3.1.0 in manifest.

# version "3.0.0"

- Optimized response transformation: minimized changes, no field renaming.
- Replaced "edges">"node" structure with "nodes" for arrays.
- Added new mutations: `cancelOrder`, `refundLineItem`, and `updateShippingAddress`.
- Added new queries: `jobStatus`, `suggestedRefund`
- Upgraded Shopify GraphQL to the latest version (April 2025).
- Changed Currency type now it's Money

# version "2.0.7"

WISMO app

- Data pull for customers and orders
- Actions: lookup_order_by_order_num, lookup_order_by_email
- Auth: headers and oauth
