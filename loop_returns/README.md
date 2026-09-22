# Loop Returns App

The Loop Returns app for Gladly automates the handling of return and exchange requests, providing agents with comprehensive return data and enabling customers to initiate returns through personalized deep links.

## Benefits

- **Increased Agent Productivity** - Frees up agents to focus on more complex, high-impact tasks rather than handling routine return or exchange requests
- **Faster Customer Resolutions** - Provides customers with direct links to complete their return or exchange, ensuring quick and seamless service
- **Enhanced Customer Experience** - Empowers customers to take action independently, reducing wait times and improving satisfaction
- **Operational Efficiency** - Reduces the volume of repetitive inquiries handled by agents, optimizing workflow and team bandwidth
- **Quick Access to Return History** - View a snapshot of the ten most recent returns, including order numbers and statuses
- **Richer Context for Better Interactions** - See details on returned items, exchanged items, pricing, and shipping statuses

## Example Use Cases

- A customer contacts support asking for the status of their return. Sidekick pulls their most recent return, provides current data on when the return was processed, and tells the customer that they have been credited.
- A customer wishes to initiate a return. Sidekick asks onboarding details to identify the customer, clarifies the order the customer wishes to return, and provides a personalized link to the Loop Returns portal for the customer's order.
- An agent needs to add a customer's email address to the allowlist to enable them to create returns outside of the standard eligibility through the Loop Returns portal.

## Available Actions

The Loop Returns app supports the following management actions:

- **Create Return Deep Link**  
  Generates a personalized deep link URL that directs customers to the Loop Returns portal with their specific order information for streamlined returns and exchanges.

- **Create Allowlist Item**  
  Creates an allowlist entry to enable customers to submit returns that would otherwise be ineligible due to time restrictions, order value, or other criteria. This allows agents to grant exceptions for customers who need to return items outside of standard return policies.

### Creating a Return

> **Beta:** The return-creation actions below are powered by Loop's Return Create (Draft Returns) API, which is in beta and not enabled by default. Contact your Loop representative to have it activated for your shop before using these actions.

Beyond the deep link, the app also supports building and submitting a return directly, step by step, without sending the customer to the Loop portal:

- **Check Eligibility**  
  Checks return eligibility for an order and starts a draft return in the same step. Returns the draft return ID (needed for every later step), which order line items are eligible or ineligible to return, the order's full line items, and the available return methods (e.g. mail-in label, drop-off).

- **Get Draft Return**  
  Looks up a draft return's current state and the next legal steps available on it. Once a returning item is set to type `exchange`, it also returns the available exchange variants (size/color) for that item — each with its price and in-stock availability — used to power same-product variant exchanges.

- **Add Return Item**  
  Adds an eligible order line item to the draft return.

- **Set Return Reason**  
  Sets the return reason for an item that's been added to the draft.

- **Set Return Type**  
  Sets whether a returning item resolves to store credit/refund or an exchange.

- **Add Exchange Item**  
  Adds the replacement item for an exchange. Exchanges are limited to a different variant of the *same product* (e.g. a different size or color); choose the replacement variant from the exchange variants returned by Get Draft Return.

- **Finalize Items**  
  Finalizes the returning items on a draft return once every item has an item, reason, and type (and exchange item, if applicable) set.

- **Get Draft Totals**  
  Reads a draft return's totals after finalizing — the amount still due (e.g. an exchange handling fee or price difference), the exchange fee, and whether the difference is covered. Lets an agent detect a balance before submitting, since a draft with an uncovered amount due can't be submitted.

- **Set Credit Type**  
  For credit/refund returns, sets whether the customer receives a refund to their original payment method or store credit/gift card.

- **Select Return Method**  
  Selects the return method (e.g. mail-in label, drop-off) for the draft return, from the options returned by Check Eligibility.

- **Submit Return**  
  Submits the finalized draft return, creating the return in Loop. If the merchant has return notifications enabled, Loop emails the customer.

- **Cancel Draft Return**  
  Cancels/abandons a draft return that hasn't been submitted yet.

- **Get Return Details**  
  Retrieves full details of an existing (already-submitted) return by Loop return ID, Loop order ID, or Shopify order name — including status, line items, refund/exchange totals, and shipping/label information.

## Data Pulls

### What Data Is Available?

With Loop Returns, you get a complete picture of each customer's return history, including:

- Return status (closed or open) and timestamps
- Currency and pricing information
- Customer email and order details
- Return items with product information, SKUs, and return reasons
- Exchange details with product totals and item information
- Cost breakdown (handling fees, gift cards, refunds, upsells)
- Shipping information (carrier, tracking, label status)

This gives your agents a real-time, single view of every customer's return journey—so you can answer questions, resolve issues, and provide accurate return status updates quickly.

### How Does Customer Matching Work?

Loop Returns finds the right customer by matching data from their customer profile:

- Primary email address
- Any email addresses
- Primary phone number
- Any phone numbers marked as `mobile`

If exactly one match is found, you'll see their returns. If no match or more than one match is found, no customer's data will be returned by Data Pull.

# Loop Returns App Toolkit

## Who maintains the integration

The Loop Returns integration is built and maintained by Gladly.

## How the integration works

The Loop Returns App integrates with the Loop Returns platform via its REST API ([API documentation](https://docs.loopreturns.com/reference/createreturndeeplink)). This allows Gladly to perform queries (like retrieving return data) and mutations (like creating return deep links). You can find out more about Loop Returns' APIs at [docs.loopreturns.com](https://docs.loopreturns.com).

Authentication is handled via API tokens included in the request headers.

## Setup & Installation

### Before You Start

- Loop Returns account with API access
- API credentials from Loop Returns
- To use the return-creation actions, Loop's **Return Create (Draft Returns) API** activated for your shop — it is in beta and not enabled by default, so contact your Loop representative to turn it on

### Installation

For detailed setup instructions, please refer to our [Loop Returns Integration Setup Guide](https://help.gladly.com/docs/set-up-loop-returns-integration). This guide provides step-by-step instructions for configuring the integration in your Gladly instance.

When configuring the app in Gladly, you'll enter your API token. Make sure your Loop API token has the scopes the actions you plan to use require:

- **Draft Returns (read + write)** — needed for the return-creation workflow (Check Eligibility through Submit Return/Cancel Draft Return).
- **Returns (read)** — needed for Get Return Details.

Loop's Return Create (Draft Returns) API is in beta and is not enabled by default. Contact your Loop representative to have it activated for your shop; until then, the return-creation actions above will return errors.

## Loop Returns Custom App

If you want to dive deeper into the technical details of the app you can find it in our [app-platform-examples repo](https://github.com/gladly/app-platform-examples). You can always clone it and adapt it to your needs.
