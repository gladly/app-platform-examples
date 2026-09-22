# Recurly App Overview
The platform for managing subscriptions, automating billing, and optimizing recurring revenue.
[Recurly V3 API (v2021-02-25)](https://recurly.com/developers/api/v2021-02-25/)


## Benefits

Used with Gladly AI, the Recurly app enables your customers to manage their subscription-related queries, such as viewing their latest billing details, checking payment status, updating account information, or managing their subscription plans and renewals.

## Features

- View a customer's Recurly subscriptions directly in Gladly AI — plan, status, renewal and lifecycle dates, and a charge breakdown (add-ons, discounts, pending changes).
- Manage subscriptions from Gladly AI: cancel, pause, resume, reactivate, and terminate.
- Look up a subscription's details by ID.

# Recurly App Toolkit

## Who maintains the integration

The Recurly integration is built and maintained by Gladly.

## Basic scope

This app provides methods of reading Account and Subscription data from Recurly, actions to manage a customer's subscriptions, and an agent-facing card that displays a customer's subscriptions in Gladly AI. These can be used in Gladly AI to directly answer and act on customer questions.


## Configuration

no specific configuration yet

# Implementation Details

## Data pull: Account and Subscriptions retrieval

**Data**

Customer matching:
Search for customers matching any of Gladly's customer's emails, for the email we are using exact match

Recurly `Account` (Customer) documentation and all available fields (of which some will not be available in Gladly by default), can be found [here](https://recurly.com/developers/api/v2021-02-25/#tag/account). Ensure you are viewing the documentation for correct API version.

Subscription mapping:
Account subscriptions are sorted by the most recently updated. Only subscriptions belonging to a customer matched in account data pull are currently retrieved, i.e.

Recurly `Subscription` documentation and all available fields (of which some will not be available in Gladly by default), can be found [here](https://recurly.com/developers/api/v2021-02-25/#tag/subscription)

**Data available by default in Gladly**

See [`app/data/data_schema.graphql`](app/data/data_schema.graphql).



**Errors**

Since this is Data Pull it's assumed there is no error client and if any Recurly errors are encountered they will not be returned to the client. Hence all errors should be ignored.


## Actions

All actions operate on a subscription and return the updated `Subscription` (or a structured error).

- **`lookup_subscription_by_id`** — Retrieve a subscription by its ID.
- **`cancel_subscription`** — Cancel a subscription. It continues through the current billing cycle and then expires; it can be reactivated until the cycle ends.
- **`reactivate_subscription`** — Reactivate a canceled subscription, returning it to an active, renewing state. Expired or failed subscriptions cannot be reactivated.
- **`pause_subscription`** — Pause a subscription for a given number of billing cycles. Set the cycle count to `0` to cancel a pending pause.
- **`resume_subscription`** — Immediately resume a paused subscription.
- **`terminate_subscription`** — Immediately expire a subscription, with an optional refund of `full` (default), `partial`, or `none`. Unlike cancellation, a terminated subscription cannot be reactivated.

## Agent UI

**Display card**

- **`recurly-subscriptions`** — A card shown in Gladly AI that lists a customer's subscriptions, each with plan, status, renewal/lifecycle dates, and a charge breakdown (add-ons, discounts, pending changes).

**Forms** (available to agents via the Gladly AI "+" menu)

Each form presents a dropdown filtered to only the subscriptions eligible for that operation, so an agent can't apply an action to an ineligible subscription.

- **`cancel-subscription`** — Cancel an active or paused subscription (`cancelSubscription`).
- **`resume-subscription`** — Resume a paused subscription (`resumeSubscription`).
- **`reactivate-subscription`** — Reactivate a canceled subscription (`reactivateSubscription`).


## Authentication

[Recurly doc](https://recurly.com/developers/api/v2021-02-25/#section/Authentication)

- Get or create a private API key in your Recurly site under **Integrations > API Credentials**