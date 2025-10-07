# Ordergroove App Overview

**Version:** 1.0.0  
**Author:** gladly.com

Used with Gladly Sidekick, the Ordergroove app empowers customers to manage subscriptions and related data. It allows customers to perform actions like cancelling subscriptions, reactivating subscriptions, skipping orders, and retrieving subscription details, reducing resolution time for subscription-related inquiries.

## Features

The Ordergroove integration provides the following core functionality:

1.  **Retrieve Data via Actions:**
    - Fetch comprehensive subscription details for a customer based on their customer ID (`getSubscriptionsByCustomerId`)
    - Retrieve a single subscription by its ID (`getSubscriptionById`)
    - Get all available cancellation reasons for subscriptions (`getCancellationReasons`)
    - Retrieve order items for a specific order (`getOrderItems`)
    - Get shipping address details by address ID (`getShippingAddress`)
    - _(Available data includes subscription status, frequency, payment info, shipping addresses, and customer details)_
2.  **Manage Subscriptions via Actions:**
    - Cancel Subscription (`cancelSubscription`)
    - Reactivate Subscription (`reactivateSubscription`)
    - Skip Subscription Order (`skipSubscription`)

# Ordergroove App Toolkit

## Who maintains the integration

The Ordergroove integration is built and maintained by Gladly.

## How the integration works

The Ordergroove App integrates with the Ordergroove platform via its REST API ([Ordergroove API Documentation](https://developer.ordergroove.com/)). This allows Gladly to perform GET requests to retrieve subscription, address, and payment data, and PATCH requests to modify subscriptions. Authentication is handled via API keys included in request headers.

## Configuration

To use this app, you need to configure the following Lookup Adapter Custom Attributes:

- `ORDERGROOVE_API_KEY`: Your API key for authentication with the Ordergroove platform.

# Implementation Details

## Data Ingestion and Synchronization

The Ordergroove App provides comprehensive data synchronization capabilities, automatically pulling and syncing customer, subscription, order, and product data from Ordergroove into Gladly.

### Data Entities

The app synchronizes the following data types:

#### Customers (`ordergroove_customer`)
- **Purpose**: Customer profile information from Ordergroove
- **Key Fields**: ID, merchant, first_name, last_name, email, phone_number, created, last_updated
- **Relationships**: Links to subscriptions owned by the customer
- **Sync Method**: Incremental sync based on `last_updated` timestamp

#### Subscriptions (`ordergroove_subscription`)  
- **Purpose**: Active and canceled subscription details
- **Key Fields**: customer, product, payment, shipping_address, frequency settings, status, pricing
- **Relationships**: Links to parent customer, associated orders, and product details
- **Sync Method**: Incremental sync with parent-child relationships to customers

#### Orders (`ordergroove_order`)
- **Purpose**: Order transaction history and details
- **Key Fields**: customer, payment, shipping_address, totals, status, created, updated
- **Relationships**: Links to parent subscription and associated items
- **Sync Method**: Incremental sync linked to subscription data

#### Items (`ordergroove_item`)
- **Purpose**: Individual line items within orders
- **Key Fields**: offer, order, subscription, product, quantity, pricing, first_placed
- **Relationships**: Links to parent order and associated product details
- **Sync Method**: Pulled as child records of orders

#### Products (`ordergroove_product`)
- **Purpose**: Product catalog and subscription-eligible items
- **Key Fields**: merchant, name, price, sku, autoship_enabled, subscription settings
- **Sync Method**: Full catalog sync with support for discontinued products

### Data Flow Architecture

1. **External ID Mapping**: Uses customer IDs, subscription IDs, and order IDs from Ordergroove as external identifiers
2. **Incremental Updates**: Leverages `updated_at` and `last_updated` timestamps for efficient data synchronization  
3. **Relationship Mapping**: Maintains parent-child relationships (Customer → Subscriptions → Orders → Items)
4. **Data Freshness**: Configurable sync intervals ensure data stays current with Ordergroove platform

### Sync Configuration

The data sync process requires the following configuration:

- `ORDERGROOVE_API_KEY`: Authentication for accessing Ordergroove data APIs
- **Sync Frequency**: Configurable through Gladly's data sync settings
- **Data Retention**: Historical data preserved according to retention policies

## Actions

### Subscription Management

#### Description

This section covers all subscription-related actions that allow customers to view and modify subscriptions.

**Available Actions:**

- `getSubscriptionsByCustomerId(customerId: String!)`: Retrieve all subscriptions for a customer
- `getSubscriptionById(subscriptionId: String!)`: Retrieve a single subscription by its ID
- `getCancellationReasons()`: Retrieve all available cancellation reasons (standard, comprehensive, and merchant-specific lists)
- `getOrderItems(orderId: String!)`: Retrieve all items for a given order ID
- `getShippingAddress(addressId: String!)`: Retrieve shipping address details by address ID

**Output:**

- `Subscription`: Complete subscription details including frequency, payment info, shipping address, status, and timestamps
- `OrderItem`: Order item information including product details, quantity, pricing, and subscription associations
- `ShippingAddress`: Customer address information including contact details and location
- `CancellationReasonResponse`: Cancellation reason response with standard and comprehensive lists
- `CancellationReason`: Cancellation reason information including code and description

**Result:**

- Agents can view comprehensive subscription data to understand customer's subscription status and history
- Order item information helps agents understand what products are included in specific orders
- Shipping address information helps agents provide accurate support and verify delivery details
- Cancellation reason data helps agents provide appropriate cancellation options to customers with standard, comprehensive, and merchant-specific reason lists

#### API Implementation

- **API:** GET /subscriptions/?customer={customerId}
- **API:** GET /subscriptions/{subscriptionId}/
- **API:** GET /cancel_reasons/
- **API:** GET /merchant/cancel_reason/
- **API:** GET /orders/{orderId}/items/
- **API:** GET /shipping_addresses/{addressId}/
- **Documentation:** [Ordergroove API Documentation](https://developer.ordergroove.com/)

### Get Cancellation Reasons

#### Description

This action retrieves all available cancellation reasons that can be used when cancelling subscriptions in Ordergroove. It provides standard cancellation reasons, a comprehensive list of all possible cancellation reasons, and merchant-specific cancellation reasons.

**User Input:**

- No input required

**Output:**

- `CancellationReasonResponse`: Object containing standard, comprehensive, and merchant-specific cancellation reason lists

**Result:**

- Agents can see standard, comprehensive, and merchant-specific cancellation reason codes and their descriptions
- This helps agents provide appropriate cancellation options to customers
- The cancellation reason codes can be used with the `cancelSubscription` action
- Standard reasons (1-10) are commonly used, comprehensive list (1-60) includes all possible scenarios, and merchant reasons (100-120) are for merchant-initiated cancellations

**Example Response:**

```json
{
  "standard_cancellation_reasons": [
    {
      "code": "1",
      "description": "Customer Request"
    },
    {
      "code": "2",
      "description": "Fraud"
    },
    {
      "code": "3",
      "description": "Payment Issues"
    }
  ],
  "all_cancellation_reasons": [
    {
      "code": "1",
      "description": "Customer Request"
    },
    {
      "code": "2",
      "description": "Fraud"
    },
    {
      "code": "3",
      "description": "Payment Issues"
    },
    {
      "code": "11",
      "description": "Product Discontinued"
    },
    {
      "code": "12",
      "description": "Seasonal Product"
    }
  ],
  "merchant_cancellation_reasons": [
    {
      "code": "100",
      "description": "Merchant Request"
    },
    {
      "code": "101",
      "description": "Product Discontinued by Merchant"
    },
    {
      "code": "102",
      "description": "Inventory Issues"
    }
  ]
}
```

#### API Implementation

- **API:** GET /cancel_reasons/
- **API:** GET /merchant/cancel_reason/
- **Documentation:** [Ordergroove Cancellation Reasons](https://help.ordergroove.com/hc/en-us/articles/360046017813-Subscriptions-Cancel-Reasons)

### Cancel Subscription

#### Description

This action allows cancelling a specific customer subscription in Ordergroove.

**User Input:**

- `subscriptionId`: Subscription ID (to identify which subscription to cancel)
- `cancelReasonCode`: Reason code for cancellation
- `cancelReasonDetails`: Detailed reason for cancellation

**Output:**

- `Boolean`: Success status indicating whether the cancellation was successful

**Result:**

- The subscription is marked as cancelled in Ordergroove
- The cancellation reason is recorded for tracking purposes

**Note:** Cancelled subscriptions cannot be reactivated through this action.

#### API Implementation

- **API:** PATCH /subscriptions/{subscriptionId}/cancel/
- **Documentation:** [Ordergroove Subscriptions Cancel API](https://developer.ordergroove.com/reference/subscriptions-cancel)

**Success Response Example (from transformation):**

```json
true
```



### Get Subscription by ID

#### Description

This action retrieves a single subscription by its unique subscription ID.

**User Input:**

- `subscriptionId`: The unique identifier of the subscription to retrieve

**Output:**

- `Subscription`: Complete subscription details including frequency, payment info, shipping address, status, and timestamps

**Result:**

- Agents can view detailed information about a specific subscription
- Helps agents provide targeted support for individual subscription issues

#### API Implementation

- **API:** GET /subscriptions/{subscriptionId}/
- **Documentation:** [Ordergroove API Documentation](https://developer.ordergroove.com/)

### Get Order Items

#### Description

This action retrieves all items associated with a specific order ID, providing detailed information about each product in the order.

**User Input:**

- `orderId`: The unique identifier of the order to retrieve items for

**Output:**

- `[OrderItem]`: Array of order item objects containing product details, quantities, pricing, and subscription associations

**Result:**

- Agents can see all products included in a specific order
- Helps agents understand order composition and resolve product-specific issues
- Provides information about subscription associations for each item

#### API Implementation

- **API:** GET /orders/{orderId}/items/
- **Documentation:** [Ordergroove API Documentation](https://developer.ordergroove.com/)

### Get Shipping Address

#### Description

This action retrieves detailed shipping address information by address ID.

**User Input:**

- `addressId`: The unique identifier of the shipping address to retrieve

**Output:**

- `ShippingAddress`: Complete address information including customer details, contact information, and location

**Result:**

- Agents can view complete shipping address details
- Helps agents verify delivery information and assist with shipping-related inquiries
- Provides customer contact information associated with the address

#### API Implementation

- **API:** GET /shipping_addresses/{addressId}/
- **Documentation:** [Ordergroove API Documentation](https://developer.ordergroove.com/)

### Reactivate Subscription

#### Description

This action reactivates a previously canceled or inactive subscription.

**User Input:**

- `subscriptionId`: Subscription ID to reactivate
- `every`: Frequency value (e.g., 1, 2, 3)
- `everyPeriod`: Period type (1=days, 2=weeks, 3=months, 4=years)
- `startDate`: New start date for the subscription (YYYY-MM-DD format)

**Output:**

- `Subscription`: Updated subscription object with new active status and frequency settings

**Result:**

- The subscription is reactivated in Ordergroove with the specified frequency and start date
- Customers can resume receiving products according to the new schedule

#### API Implementation

- **API:** PATCH /subscriptions/{subscriptionId}/reactivate/
- **Documentation:** [Ordergroove API Documentation](https://developer.ordergroove.com/)

### Skip Subscription Order

#### Description

This action allows skipping the next scheduled order by removing all subscription items from the given order and rescheduling them for the following delivery cycle. This is useful when customers want to delay their next shipment without canceling their subscriptions.

**User Input:**

- `orderId`: The unique identifier of the order to skip

**Output:**

- `Order`: Updated order object after subscription items have been removed and rescheduled

**Result:**

- All subscription items in the specified order are removed and rescheduled for the next delivery cycle
- The order is effectively skipped while keeping all subscriptions active
- Useful for customers who are traveling, have excess inventory, or want to adjust delivery timing

**Use Cases:**
- Customer is traveling and wants to skip one delivery
- Customer has excess inventory and wants to delay next shipment  
- Customer wants to adjust delivery timing without canceling subscriptions

#### API Implementation

- **API:** PATCH /orders/{orderId}/skip/
- **Documentation:** [Ordergroove Skip Subscription API](https://developer.ordergroove.com/reference/skip-subscription)

# Testing

## Setup

1.  Clone the repository.
2.  Create a `.env` file in the app's root directory (e.g., `ordergroove/app/.env`).
3.  Add the required secrets to the `.env` file:
    ```
    ORDERGROOVE_API_KEY=your_api_key_value
    ```

## Available Test Data

Each action includes comprehensive test cases located in their respective `_test_/data/` directories:

### Actions with Test Coverage

- **getSubscriptionsByCustomerId**: Tests for empty responses, successful responses, special characters, and cancelled subscriptions
- **getSubscriptionById**: Tests for successful retrieval and not found scenarios  
- **getCancellationReasons**: Integration test data available
- **cancelSubscription**: Tests for success cases and empty cancel reasons scenarios
- **getOrderItems**: Tests for empty responses, successful responses, special characters, and edge cases
- **getShippingAddress**: Tests for minimal addresses, special characters, and successful responses
- **reactivateSubscription**: Integration test data available  
- **skipSubscription**: Tests for 400/403/404 errors and successful skip scenarios

### Data Sync Testing

- **customers**: Tests for no customers found scenarios
- **subscriptions**: Tests for empty subscriptions and missing customer scenarios  
- **orders**: Tests for no orders found, missing subscription, and successful responses
- **items**: Tests for no items found, multiple items, API errors, and edge cases

## Running Tests

Tests can be executed using the Gladly App Platform testing framework. Each action contains:

- **Unit Tests**: Located in `_test_/data/` directories with expected inputs, outputs, and transformations
- **Integration Tests**: Available for live API testing with proper credentials configured

### Test Structure

Each test case includes:
- `inputs.json`: Input parameters for the action
- `rawData.json`: Mock API response data  
- `expected_response_transformation.json`: Expected transformed output
- `expected_request_url.txt`: Expected API endpoint URL
- `integration.json`: Integration test configuration

# Troubleshooting

- **Authentication Errors:** Ensure the API key in the `.env` file (for local testing) or Gladly Custom Attributes (for deployed app) is correct and has the necessary permissions in Ordergroove.
- **Action Failures:** Check the response provided by the action. If an error occurs, the action will return `false` or specific error details might be included in the response.
- **Subscription Not Found:** Verify that the subscription ID is correct and the subscription exists in the Ordergroove system.
- **Invalid Payment/Address IDs:** Ensure that the payment method or address ID exists and is associated with the customer's account.
