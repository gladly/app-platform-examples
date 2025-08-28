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

### Installation

For detailed setup instructions, please refer to our [Loop Returns Integration Setup Guide](https://help.gladly.com/docs/set-up-loop-returns-integration). This guide provides step-by-step instructions for configuring the integration in your Gladly instance.

## Loop Returns Custom App

If you want to dive deeper into the technical details of the app you can find it in our [app-platform-examples repo](https://github.com/gladly/app-platform-examples). You can always clone it and adapt it to your needs.
