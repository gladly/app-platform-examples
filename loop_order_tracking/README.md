# Loop Order Tracking (formerly Wonderment) App for Gladly

The Loop Order Tracking app for Gladly provides detailed order and shipment tracking information, displaying order and shipment data in the Conversation sidebar for agents to view and share with customers.

## Benefits

**Reduce WISMO Inquiries**  
Agents can quickly access order and shipment tracking information directly in the Conversation sidebar, allowing them to answer "Where Is My Order?" (WISMO) inquiries quickly and accurately, freeing up time for more complex issues.

**Complete Order Visibility for Agents**  
Agents can view order and shipment tracking information anytime, directly in the Conversation sidebar, without needing to switch between systems or ask customers to check external tracking pages.

**Complete Order Visibility**  
Agents and customers get a comprehensive view of order details, including shipment status, tracking codes, and carrier information, all in one place.

## Example Use Cases

- An agent opens a Conversation and sees the customer's order information displayed in the sidebar, including all associated shipments, tracking codes, and current delivery status from Loop Order Tracking.
- A customer reports that their package tracking shows a specific status. The agent can view the detailed shipment information in the Conversation sidebar, including carrier details and tracking codes.
- An agent needs to quickly view a customer's order history and shipment status to resolve a delivery question without leaving the Conversation.

## Data Pulls

### What Data Is Available?

With Loop Order Tracking, you get a complete picture of each customer's orders, including:

- Order name/identifier
- Shipment details (tracking codes, status, carrier display names)
- Shipment URLs for tracking
- Real-time order and shipment status

This gives your agents a real-time, single view of every customer's order and shipment journey—so you can answer questions and resolve issues quickly.

### How Does Customer Matching Work?

Loop Order Tracking finds the right customer by matching data from their customer profile:

- Primary email address
- Any email addresses

If the customer profile has at least one email address, Loop Order Tracking will query for orders associated with those email addresses. If no email addresses are found, no order data will be returned.

# Loop Order Tracking App Toolkit

## Who maintains the integration

The Loop Order Tracking integration is built and maintained by Loop.

## How the integration works

The Loop Order Tracking App integrates with the Loop Order Tracking platform to query order and shipment information for customers. The app fetches order data based on customer email addresses, providing real-time tracking information including shipment status, tracking codes, and carrier details.

The app displays this information in the Conversation sidebar, allowing agents to quickly view and share order and shipment details with customers without leaving the Conversation.

Authentication is handled via an API Token included in the request headers.

## Setup & Installation

### Before You Start

You will need:
- A Loop Order Tracking API access token (request from the Loop team)
- Gladly instance with App Store access

### Installation

1. **Obtain API Token**
   - Request a Loop Order Tracking API access token from the Loop team

2. **Build the App**
   - Use the `appcfg` CLI tool to validate and build the app:
     ```bash
     appcfg validate -r loop-order-tracking
     appcfg build -r loop-order-tracking
     ```
   - This will create a zip file (e.g., `loop-loop-order-tracking-1.7.5.zip`) in the current directory based on the version in `manifest.json`

3. **Install the App**
   - Install the built app using the zip file (replace with the actual filename created):
     ```bash
     appcfg apps install --filepath='loop-loop-order-tracking-1.7.5.zip'
     ```
   - For specific Gladly environments, you can include additional parameters:
     ```bash
     appcfg apps install --gladly-host='your-gladly-host' --token='your-token' --user='your-user' --filepath='loop-loop-order-tracking-1.7.5.zip'
     ```

4. **Configure Integration Secrets**
   - Create and activate an app configuration with your API token:
     ```bash
     appcfg apps config create loop/loop-order-tracking/v1.7.5 --name="Your Config Name" --config="{}" --secrets='{"apiToken":"your_api_token_here"}' --activate
     ```
   - Replace `v1.7.5` with the actual version from your `manifest.json` (format: `loop/loop-order-tracking/v{version}`)

### Required Parameters

The Loop Order Tracking app requires the following integration secret:

- **`apiToken`** (required): Your Loop Order Tracking API access token. Request this token from the Loop team. This token is used to authenticate all API requests to Loop.

## Development

### Testing the Integration

You can test the data pull functionality using appcfg:

```bash
# Validate the app structure
appcfg validate -r loop-order-tracking

# Test data pull configuration
appcfg validate data -r loop-order-tracking

# Run a data pull test
appcfg run data-pull -s '{"apiToken": "your_api_token_here"}' -d success -r loop-order-tracking
```

### Running Data Pulls

To test data pulls with specific customer data:

```bash
appcfg run data-pull -s '{"apiToken": "your_api_token_here"}' -d <test_data_directory> -r loop-order-tracking
```

## Loop Order Tracking Custom App

If you want to dive deeper into the technical details of the app you can find it in our [app-platform-examples repo](https://github.com/gladly/app-platform-examples). You can always clone it and adapt it to your needs.

