# Recurly App for Gladly Sidekick

The Recurly app for Gladly Sidekick empowers your team and your customers with seamless subscription management—right inside Gladly. With Recurly, agents can quickly look up customer accounts and subscriptions, while customers can self-serve common requests (e.g. pause a subscription), reducing agent workload and improving satisfaction.

## Benefits

**Streamlined Subscription Management**  
Enable agents to view customer subscriptions, billing details, and account information directly from Gladly.

**Faster Issue Resolution**  
Give your agents a real-time, single view of each customer's subscription journey, so they can answer questions, resolve issues, and spot upsell opportunities quickly.

**Reduce Agent Workload**  
Let customers self-serve common subscription management tasks, reducing repetitive inquiries and freeing up agent time for more complex needs.

**Drive Retention and Revenue**  
Empower your agents to proactively engage subscribers with personalized offers and timely interventions, reducing churn and increasing customer lifetime value.

## Example Use Cases

- A customer wants to pause their subscription for a period. Sidekick processes the pause and confirms the new status.
- A customer requests to cancel their subscription. Sidekick processes the cancellation and provides confirmation.
- An agent needs to quickly look up a customer's subscription status and billing history to resolve a payment question.
- A customer wishes to reactivate a previously cancelled subscription. Sidekick guides them through the process and confirms the reactivation.

## Available Actions

The Recurly app supports the following subscription management actions:

- **Lookup Subscription by ID**  
  Retrieve detailed information about a specific subscription, including status, plan, billing, and key dates.

- **Cancel Subscription**  
  Cancel a customer's subscription. The subscription will continue through its current billing cycle and then expire.

- **Pause Subscription**  
  Temporarily pause a customer's subscription for a specified number of billing cycles.

- **Resume Subscription**  
  Resume a paused subscription, moving it back to the active state.

- **Reactivate Subscription**  
  Bring a cancelled subscription back to an active, renewing state (if still eligible).

## Data Pulls

### What Data Is Available?

With Recurly, you get a complete picture of each customer, including:

- Customer account details (name, email, company, address)
- All active and past subscriptions
- Subscription status (active, paused, cancelled, expired, etc.)
- Plan details for each subscription
- Billing and shipping information
- Add-ons, coupons, and discounts applied
- Key dates (start, end, trial, pause, renewal, etc.)
- Payment and invoice status

This gives your agents a real-time, single view of every customer's subscription journey—so you can answer questions, resolve issues, and spot upsell opportunities quickly.

Note: Only subscriptions belonging to a customer matched by email are retrieved. Data is sorted by the most recently updated subscriptions.

### How Does Customer Matching Work?

Recurly finds the right customer by matching data from their customer profile:

- Primary email address (exact match)
- If no primary email, the first email in the list of email addresses

If exactly one match is found, you'll see their account and subscriptions. If no match or more than one match is found, no customer's data will be returned by Data Pull.

## Recurly App Toolkit

### Who maintains the integration

This version of Recurly integration is available to all Gladly customers and is maintained by Gladly. However, if you choose to install this app from source code you take responsibility for its maintenance.

### How the integration works

The Recurly App integrates with the Recurly platform via its [REST API (v2021-02-25)](https://recurly.com/developers/api/v2021-02-25/). This allows Gladly to perform queries (like retrieving account and subscription details) and mutations (like pausing or cancelling a subscription). You can find out more about Recurly's APIs at [Recurly API documentation](https://recurly.com/developers/api/v2021-02-25/).

Authentication is handled via private API keys included in the request headers.

## Setup & Installation

### Before You Start

- A Recurly account with a Role that includes the Integration permission (typically Admin)
- A private API key from your Recurly account

### Installation

To obtain your Recurly API key:

1. Log in to your Recurly account as a user with the Integration permission.
2. Navigate to **Integrations > API Credentials**.
3. Click **Add Private API Key**.
4. Assign a name and purpose for the key, and specify the third-party application (Gladly).
5. Click **Save Changes** and copy the API key.  
   _Note: API keys grant full access to your Recurly account. Store them securely and never expose them publicly._

For more details, see the [official Recurly API key documentation](https://docs.recurly.com/docs/api-keys).

## Recurly Custom App

If you want to dive deeper into the technical details of the app you can find it in our [app-platform-examples repo](https://github.com/gladly/app-platform-examples). You can always clone it and adapt it to your needs.
