# Yotpo App for Gladly

INTERNAL DOCUMENTATION. DO NOT COPY DIRECTLY TO app-platform-examples

## Overview

The Yotpo app enables Gladly users to seamlessly retrieve and view customer profile, loyalty, and review data from Yotpo directly within Gladly. This integration helps agents quickly access rich customer insights, improving support and engagement workflows.

## Features & Use Cases

- **Customer Profile Lookup:**
  - Fetch comprehensive customer details from Yotpo using Gladly customer emails and mobile phone numbers.
  - Data includes: customer ID, contact info, name, gender, account status, tags, marketing preferences, and more.
- **Loyalty Data:**
  - Retrieve Yotpo loyalty program details (points, VIP tier, spend, perks, etc.) for each matched customer.
- **Reviews Data:**
  - Retrieve up to 10 reviews per customer by default (configurable via `reviewLimit` parameter), including unpublished reviews, for a complete view of customer feedback and engagement.

**Example Use Cases:**

- Agents can instantly identify VIP customers and their loyalty status to provide premium support experiences.
- Prioritize customers based on loyalty tier and review sentiment to provide appropriate support levels.
- See recent reviews to understand customer sentiment and history.

## Available Actions & Data Pulls

### Data Pulls

- **Customer Profile Data Pull**
  - Looks up Yotpo customers by all Gladly emails and mobile phone numbers.
  - Validates last name for more accurate matching.
  - Returns detailed customer profile, including UGC summary (reviews, ratings, sentiment).
  - **API:**
    - `GET https://developers.yotpo.com/core/v3/stores/{store_id}/customers?email={email}&access_token={access_token}&limit=5&expand=reviews`
    - `GET https://developers.yotpo.com/core/v3/stores/{store_id}/customers?phone_number={number}&access_token={access_token}&limit=5&expand=reviews`

- **Loyalty Data Pull**
  - For each matched customer with an email, retrieves loyalty program data including referral code details.
  - **API:**
    - `POST https://developers.yotpo.com/loyalty/v3/stores/{store_id}/customers?access_token={access_token}`
    - Request body: `{ "customers": { "customer_email": "<email>" }, "with_referral_code": true }`

- **Reviews Data Pull**
  - For each matched customer with an email, retrieves reviews (recommended: 10, configurable via `reviewLimit`).
  - Includes unpublished reviews, site reviews, and nested data (product details, comments).
  - Reviews are sorted by date (descending).
  - **API:**
    - `GET https://developers.yotpo.com/v2/{store_id}/reviews?access_token={access_token}&customer_email={email}&count={reviewLimit}&include_unpublished_reviews=true&include_nested=true&include_site_reviews=true&sort=date&direction=desc`

### Data Schema

See `app/data/data_schema.graphql` for full details on returned fields and types.

### Customer Matching Logic

- All emails and mobile phone numbers (type "MOBILE") from Gladly are used for lookup.
- If Gladly name and Yotpo name (first or last) are present, at least one must match (case-insensitive substring).
- If no matches are found after name filtering, customers are excluded and mismatch details are logged.

## Configuration

The app uses OAuth authentication and requires the following configuration:

**Configuration:**

- `store_id` (string) - Your Yotpo Store ID, which can be found in Yotpo account settings
- `reviewLimit` (integer) - Number of reviews to fetch per customer (recommended: 10, maximum: 50)

**OAuth Credentials:**

- `client_id` - Yotpo App Key (configured automatically during OAuth flow)
- `client_secret` - Yotpo Secret Key (configured automatically during OAuth flow)
- `access_token` - Generated during OAuth authorization

**Example Configuration JSON:**

```json
{
  "integration": {
    "configuration": {
      "store_id": "your_yotpo_store_id_here",
      "reviewLimit": 10
    }
  }
}
```

> **Note:** OAuth credentials (`client_id`, `client_secret`, `access_token`) are automatically managed through the OAuth flow and generally **should not be set manually**. The only exception is when running `appcfg run oauth` for local testing.
> 
> For full details on local testing with OAuth, see the ["Testing Yotpo Locally"](#testing-yotpo-locally) section below.

## Setup and Installation

### Customer Prerequisites

Customers need to provide:

- **Store ID** (`store_id`) - Found in Yotpo Settings > General Settings

### Installation Requirements

This app requires:

- **OAuth Authorization**: A user with Yotpo admin access must complete the OAuth flow
- **Technical Resources**: Someone who can install and run the [appcfg CLI](https://github.com/gladly/app-platform-appcfg-cli)
- **Access**: Both Gladly and Yotpo administrator access

### Installation Steps

To install the Yotpo App for Gladly Sidekick, follow these steps:

1. **Gather Required Yotpo Credentials**
   Follow [Finding your Yotpo app key and secret key](https://support.yotpo.com/docs/finding-your-yotpo-app-key-and-secret-key) to obtain Yotpo app key and secret key.
   - **App Key (store_id)** – Select Account Settings > General Settings. You’ll find your App Key at the bottom of the General Settings section.

2. **[Technical] Configure the App with your store_id**

   ```bash
   appcfg apps config create "gladly.com/yotpo/v1.0.0" \
     --name "Yotpo <store name>" \
     --config '{"store_id": "<yotpo_store_id>"}'
     --secrets '{}'
   ```

   **Optional Configuration Parameters:**
   - `reviewLimit`: Number of reviews to fetch per customer (default: 10, maximum recommended: 50)

3. **[Technical] List Configurations to Get Configuration ID**
   - Install `appcfg`
   - Log into Gladly

   ```bash
   appcfg apps config list --identifier "gladly.com/yotpo/v1.0.0"
   ```

   Note the `CONFIG ID` from the output.

4. **[Technical] Run the OAuth Authorization Flow**
   Complete the OAuth authorization:

   ```bash
   appcfg apps oauth <config-id>   --gladly-host <us-uat.gladly.qa or us-1.gladly.com>
   ```

   This will:
   - Open a browser window for Yotpo OAuth authorization
   - Prompt you to authorize the app with your Yotpo admin account
   - If successful you will see a blank screen, with the text: R2xhZGx5IGhhcyBiZWVuIHN1Y2Nlc3NmdWxseSBhdXRob3JpemVk. This means that your configuration has successfully authorized.

5. \*\*[Technical] When you have completed the Oauth flow activate your app:

```
 appcfg apps config <config-id> --activate
```
## Testing Yotpo Locally

To test the Yotpo app locally, follow these steps to obtain and use an access token:

1. **Set up your `.env` file**

   Create a `.env` file in the `yotpo/` directory with your Yotpo credentials (available in 1Password):

   ```env
   YOTPO_CLIENT_ID=<your_client_id>
   YOTPO_CLIENT_SECRET=<your_client_secret>
   YOTPO_STORE_ID=<your_store_id>
   ```

2. **Run the OAuth flow**

   **Important:** You must be logged into Yotpo in your browser before running this command.

   ```bash
   make run-oauth
   ```

   This will open a browser window for the OAuth flow.

3. **Handle the Redirect (500 Error)**

   After authorizing, the redirect will return a 500 error. This is expected.
   In the browser's address bar, grab the `code` parameter from the URL (e.g., `...?code=abc123...`).

4. **Exchange the Code for an Access Token**

   ```bash
   curl -X POST https://developers.yotpo.com/v2/oauth2/token \
     -H "Content-Type: application/json" \
     -d '{
       "client_id": "<YOTPO_CLIENT_ID from .env>",
       "client_secret": "<YOTPO_CLIENT_SECRET from .env>",
       "grant_type": "authorization_code",
       "code": "<from step 3>",
       "redirect_uri": "<from 1 password>"
     }'
   ```

   This will return a JSON response containing your `access_token`.

5. **Add the Access Token to your `.env` file**
   ```env
   YOTPO_ACCESS_TOKEN=<your_access_token>
   ```

You can now run the tests and integration flows as described above using your local access token.

### Yotpo data

To view customer reviews go to: https://reviews.yotpo.com/#/moderation/reviews?date_range=2000-01-01,2025-07-17&filterType=reviews&order=desc&page=1&perPage=20&predefined=total_reviews&sort_by=review_creation_date&status=all

To view customers go to: https://loyalty-app.yotpo.com/customers

## Maintainer Information

This integration is built and maintained by Gladly.
