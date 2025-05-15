# SKIO App Overview

## Benefits

Used with Gladly Sidekick or directly by agents, the Skio app empowers support teams to manage customer subscriptions efficiently. It allows agents to quickly access subscription details and perform common actions like pausing or canceling, reducing resolution time for subscription-related inquiries.

## Features

The Skio integration provides the following core functionality:

1.  **Retrieve Subscription Data via Data Pull:** Fetch comprehensive subscription details for a customer based on their contact info (via Data Pulls). This includes status, billing dates, subscribed items, billing and delivery policy details.
2.  **Manage Subscriptions via Actions:**
    - Get Subscription by Id (`getSubscriptionById`).
    - Cancel active subscriptions (`cancelSubscription`).
    - Pause active subscriptions (`pauseSubscription`).
    - Trigger immediate, unscheduled shipments (`shipNow`).

# Implementation Details

# SKIO App Toolkit

## Who maintains the integration

The SKIO integration is built and maintained by Gladly.

## How the integration works

The Skio App integrates with the Skio platform via its [GraphQL API](https://code.skio.com/). This allows Gladly to perform queries (like retrieving subscription details) and mutations (like canceling or pausing a subscription).

Authentication is handled via API tokens included in the request headers.

## Configuration

There are currently no configuration options for the app.

# Implementation Details

## Data pull: Account and Subscriptions retrieval

### Data Pull APIs

1. **StorefrontUser Lookup**

   - Purpose: Find the customer's Skio account using their email addresses and phone numbers
   - Lookup criteria:
     - Primary email address
     - All email addresses from the customer profile
     - Primary phone number
     - Mobile phone numbers from the customer profile
   - Behavior:
     - Returns a single customer if exactly one match is found
     - Returns an error if no customer is found
     - Returns an error if multiple customers are found
   - API: GraphQL query to `StorefrontUsers` with email/phone filters
   - Data retrieved: Customer ID, platform ID, basic profile info
   - Documentation and all StorefrontUser fields can be found in [StorefrontUser API Reference](https://code.skio.com/#query-StorefrontUsers)

2. **Subscriptions Lookup**

   - Purpose: Get all subscriptions and their line items for the customer
   - API: GraphQL query to `Subscriptions` with customer ID filter, including nested `SubscriptionLines`, `BillingPolicy` and `DeliveryPolicy`
   - Data retrieved:
     - Subscription details (status, frequency, billing info)
     - Line items (products, pricing, variants)
     - Billing Policy
     - Delivery Policy
   - Dependencies: Requires StorefrontUser data
   - Documentation: [Subscriptions API Reference](https://code.skio.com/#query-Subscriptions)
   - Note: This data pull retrieves all subscriptions associated with the customer ID, regardless of their `status`. It only retrieves subscription lines that have not been removed (`removedAt` is null).

### Data Available in Gladly

- Customer profile information
- Subscriptions list
- Subscription details (status, frequency, next billing date)
- Product information for each subscription line
- Pricing and billing information
- Billing and Delivery Policies including frequency, min cycles, etc

### Errors

Since this is Data Pull it's assumed there is no error client and if any SKIO errors are encountered they will not be returned to the client.

## Actions

### Cancel Subscription

#### Description

This action allows cancelling a customer's subscription in Skio. According to the Skio documentation, this action:

- Cancels a specific subscription and associated SubscriptionContract on Shopify
- For prepaid subscriptions, calculates remaining shipments and sets maxcycles so that the subscription will be terminated once all remaining shipments are processed

**Input:**

- Subscription ID (to identify which subscription to cancel)
- ShouldSendNotif boolean indicating whether customer should be notified

**Response:**

- Confirmation of whether the cancellation was successful (via the `ok` boolean)

_Note:_ The Skio API currently returns `"ok": true` even if the provided `subscriptionId` does not exist or belongs to an already cancelled subscription.

**Result:**

- The subscription is marked as cancelled in Skio
- The associated SubscriptionContract on Shopify is cancelled
- For prepaid subscriptions, remaining shipments are calculated and maxcycles are set accordingly

#### API Implementation

- API: SKIO GraphQL mutation [`cancelSubscription`](https://code.skio.com/#mutation-cancelSubscription)

**Success Response:**

```json
{
  "data": {
    "cancelSubscription": {
      "ok": true
    }
  }
}
```

### Get Subscription By ID

#### Description

This action fetches the details of a specific subscription from Skio using its unique ID. This is useful for checking the current status (e.g., ACTIVE, CANCELLED) and other details like the next billing date or when it was cancelled.

**User Input:**

- Subscription ID (to identify which subscription to retrieve)

**Output:**

- Subscription ID
- Platform ID (e.g., Shopify GID)
- Storefront User ID
- Status (e.g., ACTIVE, CANCELLED)
- Currency Code
- Cycles Completed
- Creation Date
- Last Updated Date
- Cancellation Date (if applicable)
- Next Billing Date (if applicable)

#### API Implementation

This action uses the Skio GraphQL API's [SubscriptionByPk](https://code.skio.com/#query-SubscriptionByPk) query to fetch subscription details.

**Input Example:**

**Success Response:**

```json
{
  "id": "subscription-uuid-here",
  "platformId": "gid://shopify/SubscriptionContract/12345",
  "storefrontUserId": "customer-uuid-here",
  "status": "CANCELLED",
  "currencyCode": "USD",
  "cyclesCompleted": 1,
  "createdAt": "2024-01-01T00:00:00.123456+00:00",
  "updatedAt": "2024-02-16T12:00:00.000000+00:00",
  "cancelledAt": "2024-02-15T10:30:00.000000+00:00",
  "nextBillingDate": null
}
```

### Ship Subscription Now

#### Description

This action triggers an immediate, unscheduled shipment for a specified subscription. This is useful if a customer requests an early delivery.

**Output:**

- Confirmation Message (from Skio)
- Success Status (`ok` boolean)

**Result:**

- A new order is generated immediately for the subscription in Skio/Shopify.

#### API Implementation

This action uses the Skio GraphQL API's `shipNow` mutation.

**Success Response:**

```json
{
  "message": "Order created successfully",
  "ok": true
}
```

### Pause Subscription

#### Description

This action allows to pause a customer's active subscription in Skio. This temporarily stops the subscription from being billed and shipped until it is unpaused.

**User Input:**

- Subscription ID (to identify which subscription to pause)

**Output:**

- Confirmation message from Skio.
- Indication of whether the pause operation was successful (via the `ok` boolean).

**Result:**

- The specified subscription is marked as paused in Skio.
- Future billing and shipments for the subscription are temporarily stopped.

#### API Implementation

This action uses the Skio GraphQL API's [pauseSubscription](https://code.skio.com/#mutation-pauseSubscription) mutation.

**Success Response (Initial Pause):**

```json
{
  "message": "Subscription <ID> paused by PublicAPI user: unknown at <timestamp>",
  "ok": true
}
```

**Success Response (Already Paused):**

```json
{
  "message": "Subscription is already paused",
  "ok": true
}
```

## Authentication

This integration uses API token authentication to communicate with the Skio GraphQL API.

**Obtaining a Token:**
As described in the [Skio API Authorization Documentation](https://code.skio.com/#section/Authorization), you can generate an API token from your Skio dashboard. Navigate to the API section, give your token a name, and generate it.

**Using the Token:**
The integration secrets are part of the app configuration and should be set (or updated) using appcfg tool: [appcfg apps config](https://github.com/gladly/app-platform-appcfg-cli/blob/main/docs/appcfg_apps_config.md). Add token as `apiToken` to integration secrets.

## Additional Resources

- [Skio API Documentation](https://code.skio.com/)
- [Skio Data Model](https://code.skio.com/#data-model)
- [Skio Query Examples](https://code.skio.com/#query-examples)

# Development

Follow these steps to run integration tests:

## Setup

1. Create a `.env` file in the app directory:

   ```bash
   SKIO_API_TOKEN=your_api_token_here
   ```

   Replace `your_api_token_here` with your actual Skio API token.

2. Make sure the `.env` file is not committed to version control:
   ```bash
   echo ".env" >> .gitignore
   ```

## Running Tests

The Makefile provides several commands for testing the app:

### Basic Commands

- `make validate` - Validates the app configuration
- `make test` - Runs all tests
- `make build` - Builds the app after tests pass

### Run Data Pulls

- `make storefront-user-data-graphql` - Run storefront user data graphql query
- `make storefront-user-data-pull` - Run storefront user data pull

### Integration Tests

- `make integration-tests` - Runs all data tests in sequence, as well as available run commands against external system

## Example Usage

1. First, validate the app configuration:

   ```bash
   make validate
   ```

2. Run all integration tests:

   ```bash
   make integration-tests
   ```

3. To run a specific query:
   ```bash
   make storefront-user-data-graphql
   make get-subscription-by-id
   ```

## Troubleshooting

If you encounter any issues:

1. Ensure your API token is correctly set in the `.env` file
2. Verify that the app configuration is valid using `make validate`
