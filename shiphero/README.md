# Shiphero App

The Shiphero App integrates with the Shiphero system via [GraphQL API](https://developer.shiphero.com/getting-started)

## Features

It can be used across the Gladly platform to:

1. Retrieve shipment-related data (e.g. shipping provider, tracking information, hold reason, shipment status, etc.)
2. Lookup orders by order number, email, and track shipments by order ID.

## Configuration

We are using the following defaults:

- Where appropriate data (orders) is returned in the following order `sort: "-order_date"`. That is most recent orders are returned first
- By default we return 40 newest orders (as described above). You can change this in integration `config` by changing `ordersLimit`, e.g.

  ```
  {
    "ordersLimit": 10
  }
  ```

- Each order also contains lineItems (order items e.g., product with quantity, SKU, etc.), the quantity of which can be expanded (by default, it is set to 40 items per order). This can be configured similarly in the integration config by adjusting the `lineItemsLimit`. For example:

  ```
  {
    "lineItemsLimit": 20
  }
  ```

## Data pull: Orders retrieval

**Data**

Shiphero `Order` documentation and all available fields (of which some will not be available in Gladly by default), can be found [here](https://developer.shiphero.com/wp-content/uploads/spectaql/#definition-Order)

<details>
<summary>Data available by default in Gladly</summary>
[todo] ADD LINK to data_schema.graphql (l.1-92)
</details>

## Actions

Available Query actions:
[todo] ADD LINK to actions_schema.graphql (l.129-136)

### Error handling

Any errors received from Shiphero will be returned to client as 500.

You can read more on Shiphero Errors in [Shiphero documentation](https://developer.shiphero.com/graphql-primer/#errorhandling)

### Actions Overview:

This section provides an overview of all available actions, their inputs, outputs, and example responses.

---

#### **1. Lookup Order by Order Number**

Definition and input:
[todo] ADD LINK to actions_schema.graphql (l.132-133)

Response:
[todo] ADD LINK to actions_schema.graphql (l.48-71)

Example success response:
[todo] ADD LINK to shiphero/actions/look_up_order_by_order_num/\_test\*/data/success/expected_response_transformation.json

#### **2. Lookup Orders by Email**

Definition and input:
[todo] ADD LINK to actions_schema.graphql (l.130-131)

Response:
[todo] ADD LINK to actions_schema.graphql (l.48-71)

Example success response:
[todo] ADD LINK to shiphero/actions/look_up_orders_by_email/\_test\*/data/success/expected_response_transformation.json

#### **3. Track Shipments by Order Id**

Definition and input:
[todo] ADD LINK to actions_schema.graphql (l.134-135)

Response:
[todo] ADD LINK to actions_schema.graphql (l.113-127)

Example success response:
[todo] ADD LINK to shiphero/actions/track_shipments_by_order_id/\_test\*/data/success/expected_response_transformation.json
