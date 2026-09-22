# BigCommerce App

The BigCommerce App integrates with the BigCommerce system via [REST API](https://developer.bigcommerce.com/docs/rest-management)

## Benefits

Used with Gladly Sidekick, the BigCommerce app empowers your customers to resolve issues related to their orders, e.g. find their most recent order, check their shipping address or status of their shipments, etc

## Features

It can be used across the Gladly platform to:

1. **Retrieve Customer and Order Data**  
   Access comprehensive details about customers and their orders in the BigCommerce system, including:
   - **Customer Details**: Name, email, phone, company, and account metadata (e.g., creation date, notes).
   - **Order Details**: _Products_ (SKU, quantity, price, refund statuses), _transactions_ (payment statuses, events, gateway), _shipments_ (tracking numbers, delivery address, tracking carrier), and general _order metadata_ (creation date, payment status, total amount).
2. **Order Management Actions**  
   Perform key actions on customer orders:
   - **Refund Order**: Calculate and process refund for order.
   - **Cancel Order**: Update an order status to "canceled" with an optional cancellation reason.
   - **Create Refund Quote**: Retrieve a detailed refund quote before processing a refund to ensure the correct refund amount and tax details are calculated.

## Configuration

We are using the following defaults:

- Orders limit: Where appropriate data (orders) is returned in descending order. That is most recent orders are returned first. By default we return 10 newest orders (as described above). You can change this in integration `config` by changing `ordersLimit`, e.g.

  ```
  {
    "ordersLimit": 5
  }
  ```

- [Store hash](https://support.bigcommerce.com/s/question/0D54O00006QpiLnSAJ/where-i-get-store-hash-code?language=en_US): Required to access the store's data via the API. This can be configured to target a specific store. It should be set in integration `config`.
  ```
  {
    "store": <store_hash>
  }
  ```

## Data pull

**Data**

BigCommerce `Customer`, `Order`, `Product`, `Transactions` and `Shipments` documentation, along with all available fields (some of which are not available in Gladly by default), can be found [here](https://developer.bigcommerce.com/docs/rest-management)

<details>
<summary>Data available by default in Gladly</summary>
[todo] ADD LINK to data_schema.graphql (l.1-180)
</details>

## Actions

Available Query actions:
[todo] ADD LINK to actions_schema.graphql (l.114-126)

### Error handling

Any errors received from the data pool will be returned to the client as a 500 error.

For actions, the returned structure include the following fields for error details:

- `http_status`: The HTTP status code of the response.
- `success`: Indicates whether the action was successful (`true` or `false`).
- `error_message`: A description of the error, if applicable.

### Actions Overview:

This section provides an overview of all available actions, their inputs, outputs, and example responses.

---

#### **1. Create Refund Quote**

Definition and input:
[todo] ADD LINK to actions_schema.graphql (l.116-117)

Response:
[todo] ADD LINK to actions_schema.graphql (l.94-114)

Example success response:
[todo] ADD LINK to .../big_commerce/actions/create_refund_quote/\_test\*/data/success/expected_response_transformation.json

#### **2. Cancel Order**

Definition and input:
[todo] ADD LINK to actions_schema.graphql (l.118-119)

Response:
[todo] ADD LINK to actions_schema.graphql (l.55-74)

Example success response:
[todo] ADD LINK to .../big_commerce/actions/cancel_order/\_test\*/data/success/expected_response_transformation.json

#### **3. Refund Order**

Definition and input:
[todo] ADD LINK to actions_schema.graphql (l.120-124)

Response:
[todo] ADD LINK to actions_schema.graphql (l.39-55)

Example success response:
[todo] ADD LINK to ...big_commerce/actions/refund_order/\_test\*/data/success/expected_response_transformation.json
