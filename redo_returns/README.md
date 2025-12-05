# Redo Returns - Gladly App

A Gladly app that integrates with the Redo Returns API to display customer return information directly in the Gladly agent interface.

## Overview

This app fetches return data from the Redo API based on the customer's email address and displays it as an expandable card in Gladly. Agents can view return details including:

- Return status and creation date
- Associated orders
- Products being returned (with price, reason, and return type)
- Refund amounts
- Gift cards issued
- Financial totals (shipping, taxes, discounts, store credit, etc.)
- Tracking and exchange order links

## Project Structure

```
├── manifest.json              # App metadata (name, version, author)
├── headers/
│   └── Authorization.gtpl     # API authorization header template
├── data/
│   ├── data_schema.graphql    # GraphQL schema defining return data types
│   └── pull/returns/          # Data pull configuration
│       ├── config.json        # Pull configuration (HTTP method, data type)
│       ├── request_url.gtpl   # API endpoint URL template
│       ├── response_transformation.gtpl  # Response transformation logic
│       ├── external_id.gtpl   # External ID extraction
│       └── external_updated_at.gtpl      # Timestamp extraction
└── ui/templates/returns/      # UI card template
    ├── config.json            # Template configuration
    └── flexible.card          # Card layout definition
```

## Configuration

The app requires the following configuration to be set via `appcfg`:

| Configuration | Description |
|---------------|-------------|
| `storeId` | Redo store ID for the API endpoint. |

### Setting Configuration

Use the Gladly `appcfg` CLI to set the required configuration:

```bash
appcfg config set storeId <your-redo-store-id>
```

## Required Secrets

The app requires the following secret to be configured via `appcfg`:

| Secret | Description |
|--------|-------------|
| `apiToken` | Redo API token for authentication. Must be a valid Bearer token for the Redo API. |

### Setting Secrets

Use the Gladly `appcfg` CLI to set the required secrets:

```bash
appcfg secret set apiToken <your-redo-api-token>
```

## Building and Deploying

### Prerequisites

- [Gladly App CLI (`appcfg`)](https://developer.gladly.com/app-development/) installed
- Access to a Gladly organization with app development enabled
- A valid Redo API token

### Testing

Run the app tests using:

```bash
appcfg test
```

The test suite includes scenarios for:
- Successful return data retrieval
- Customers without email addresses
- No returns found (204 response)

### Deployment

1. Validate the app configuration:
   ```bash
   appcfg validate
   ```

2. Deploy to your Gladly organization:
   ```bash
   appcfg deploy
   ```

## API Integration

The app makes GET requests to:
```
http://localhost:8001/v2.2/stores/{storeId}/customer/returns?email={customer_email}
```

### Request Headers

- `Authorization: Bearer {apiToken}` - Uses the configured API token

### Response Handling

- **200 OK**: Returns array of return objects
- **204 No Content**: No returns found for the customer (displays empty state)
- Other status codes result in an error

## Data Schema

The app defines the following GraphQL types for return data:

- `RedoReturn` - Main return object containing:
  - `id`, `status`, `createdAt`
  - `orders` - Associated order references
  - `products` - Items being returned
  - `refunds` - Refund amounts
  - `giftCards` - Issued gift cards
  - `totals` - Financial summary
  - `trackingLink`, `exchangeOrderLink` - External links
