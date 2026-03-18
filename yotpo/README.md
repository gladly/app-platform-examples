# Yotpo App for Gladly

| Loyalty & Referrals | Reviews |
|---|---|
| ![Loyalty & Referrals card](docs/loyalty-referrals-card.png) | ![Reviews card](docs/reviews-card.png) |

## Overview

The [Yotpo](https://www.yotpo.com/) app enables Gladly users to seamlessly retrieve and view customer profile, loyalty, and review data from Yotpo directly within Gladly. This integration helps agents quickly access rich customer insights, improving support and engagement workflows.

For full product documentation, see [Yotpo on App Platform](https://help.gladly.com/docs/yotpo-on-app-platform).

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

## Maintainer Information

This integration is built and maintained by Gladly.
