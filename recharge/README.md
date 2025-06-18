# Recharge App for Gladly Sidekick

The Recharge app for Gladly Sidekick empowers your team and your customers with seamless subscription management—right inside Gladly. With Recharge, agents can quickly look up a customer's subscriptions, while customers can self-serve common requests (e.g. reactivate a subscription), reducing agent workload and improving satisfaction.

## Benefits

**Empower Customer Self-Service**  
Let customers manage their own subscriptions through Gladly Sidekick, reducing repetitive inquiries and freeing up agent time for more complex needs.

**Know Your Subscriber**  
Give your agents a holistic, single view of each customer by pulling key subscription and order details from Recharge directly into Gladly—no switching between systems required. Agents get a complete, real-time view of each customer's subscription journey.

**Enhanced Agent Productivity**  
By letting AI handle common customer inquiries, agents can focus on resolving more complex issues, dedicating more time to meaningful interactions and boosting both efficiency and satisfaction.

**Quick and Easy Setup**  
With an out-of-the-box integration built by Gladly, your team can get up and running quickly—no heavy technical resources required.

**Drive Revenue and Retention**  
Empower your agents to grow revenue and boost retention by proactively engaging subscribers with personalized subscription offers. With instant access to subscription insights, agents can reduce churn and ultimately increase customer lifetime value.

## Example Use Cases

- A customer wants to skip their next subscription shipment. Sidekick processes the request and provides confirmation.
- A customer wishes to cancel their subscription. Sidekick guides them through the process and confirms the cancellation.
- An agent needs to quickly view a customer's subscription status and history to resolve a billing or delivery question.
- A customer requests to reactivate a previously cancelled subscription. Sidekick processes the reactivation and notifies the customer.

## Available Actions

The Recharge app supports the following subscription management actions:

- **Get Subscription by ID**  
  Retrieve detailed information about a specific subscription, including status, product lines, billing and delivery policies, and key dates.

- **Cancel Subscription**  
  Cancel a customer's subscription. This marks the subscription as cancelled and handles prepaid subscriptions appropriately.

- **Reactivate Subscription**  
  Reactivate a previously cancelled subscription, resuming billing and shipments.

- **Skip Next Subscription**  
  Skip the next scheduled charge for a subscription, delaying the next shipment.

- **Unskip Next Subscription**  
  Undo a previously skipped charge, restoring the next scheduled shipment.

## Data Pulls

### What Data Is Available?

With Recharge, you get a complete picture of each subscriber, including:

- Customer name and contact info (email, phone)
- All active and past subscriptions
- Subscription status (active, cancelled, expired)
- Next charge date and billing frequency
- Products in each subscription (name, quantity, price)
- Total subscription cost and charge history
- When the subscription started and (if cancelled) when it ended
- Delivery and billing schedule details
- All addresses and charge records associated with the customer

This gives your agents a real-time, single view of every customer's subscription journey—so you can answer questions, resolve issues, and spot upsell opportunities quickly.

### How Does Customer Matching Work?

Recharge finds the right customer by matching data from their customer profile:

- Primary email address
- Any email addresses

If exactly one match is found, you'll see their subscriptions, charges, and addresses. If no match or more than one match is found, no customer's data will be returned by Data Pull.

# Recharge App Toolkit

## Who maintains the integration

This version of Recharge integration is available to all Gladly customers and is maintained by Gladly. However, if you choose to install this app from source code you take responsibility for it's maintenance.

## How the integration works

The Recharge App integrates with the Recharge platform via its [REST API](https://developer.rechargepayments.com/). This allows Gladly to perform queries (like retrieving subscription details) and mutations (like canceling, skipping, or reactivating a subscription). You can find out more about Recharge's APIs at [https://developer.rechargepayments.com/](https://developer.rechargepayments.com/).

Authentication is handled via API tokens included in the `X-Recharge-Access-Token` request header.

## Setup & Installation

### Before You Start

- Recharge account with API access
- Gladly instance with Sidekick enabled

### Installation

You will need [Recharge API Key](https://docs.rechargepayments.com/docs/recharge-api-key) to configure the app.

Make sure your token has the following permissions:

- Customers – Read and Write Access
- Subscriptions – Read and Write Access
- Products – Read Access

## Recharge Custom App

If you want to dive deeper into the technical details of the app you can find it in our [app-platform-examples repo](https://github.com/gladly/app-platform-examples). You can always clone it and adapt it to your needs.
