# Zendesk App for Gladly

## Overview

The Zendesk app provides Zendesk Support API functionality through the Gladly app platform. It supports creating and managing Zendesk users and tickets.

## Available actions

### Mutations

#### createOrUpdateCustomer

Creates or updates a Zendesk user using the Gladly Customer ID as `external_id`. This enables idempotent user resolution for both new and returning Customers.

**Parameters:**

- `user`: User details object
  - `customerId`: Gladly Customer ID (becomes `external_id` in Zendesk) (required)
  - `name`: Customer name (required)
  - `email`: Customer email address (optional)
  - `phone`: Customer phone number in E.164 format (optional)

**Response:**

- Returns `UserResult` with `user` object containing the Zendesk user ID, or `error` if the operation failed

#### createTicket

Creates a new ticket in Zendesk.

**Required parameters:**

- `subject`: The ticket subject line
- `comment`: Object containing the ticket description and visibility
  - `body`: The ticket description as plaintext (or use `html_body` for HTML)
  - `html_body`: The ticket description as HTML
  - `public`: Whether the comment is visible to the end user (default: `true`)
- One of:
  - `requester`: Object with `name` and `email` (creates a new requester)
  - `requester_id`: Zendesk user ID (uses an existing user)

**Optional parameters:**

- `submitter_id`: User ID of the person who submitted the ticket
- `priority`: `"low"`, `"normal"`, `"high"`, `"urgent"` (default: `"normal"`)
- `status`: `"new"`, `"open"`, `"pending"`, `"hold"`, `"solved"`, `"closed"` (default: `"new"`)
- `type`: `"problem"`, `"incident"`, `"question"`, `"task"` (default: `"incident"`)
- `tags`: Array of tags to add to the ticket
- `assignee_id`: Zendesk agent ID to assign the ticket to
- `group_id`: Group ID to assign the ticket to
- `ticket_form_id`: Ticket form ID to use
- `channel`: Channel source (see [Zendesk Via Types](https://developer.zendesk.com/documentation/ticketing/reference-guides/via-types/))

**Response:**

- Returns `TicketResult` with `ticket` object or `error`

#### addComment

Adds a comment to an existing Zendesk ticket.

**Parameters:**

- `ticket_id`: The ticket ID to add a comment to (required)
- `comment`: Comment object
  - `body`: Comment text (or use `html_body`)
  - `html_body`: Comment as HTML
  - `public`: Whether visible to the end user (default: `true`, set `false` for internal notes)
  - `author_id`: User ID to display as the comment author (optional)
- `channel`: Channel source (optional)

**Response:**

- Returns `TicketResult` with the updated `ticket` object or `error`

#### createEndUser

Creates a new Zendesk end-user. Returns an error if a user with a matching email already exists.

**Parameters:**

- `user`: User details object
  - `customerId`: Gladly Customer ID (required)
  - `name`: User name (required)
  - `email`: Email address (optional)
  - `phone`: Phone number (optional)

**Response:**

- Returns `UserResult` with `user` object or `error`

### Queries

#### searchUsersByPhone

Searches for Zendesk users by phone number.

**Parameters:**

- `phone`: Phone number to search for (exact match)

**Response:**

- Returns `UserSearchResult` with `count` and `users` array

### Data schema

See `app/actions/actions_schema.graphql` for full details on input types, return types, and field descriptions.

## How Zendesk works

Understanding Zendesk's data model helps with proper integration.

### Key concepts

| Concept           | Description                                                                  |
| ----------------- | ---------------------------------------------------------------------------- |
| **User**          | A profile containing name, email, phone, and other identifiers               |
| **Ticket**        | A support request linked to a user, containing conversation history          |
| **Requester**     | The user requesting help on a ticket                                         |
| **External ID**   | An optional unique identifier from another system (e.g., Gladly Customer ID) |
| **Direct Line**   | A phone number uniquely assigned to one user. Used for SMS routing.          |
| **Shared Number** | A phone number linked to multiple users. Cannot be used for SMS routing.     |

### User identification

Zendesk identifies users through several mechanisms:

- **Email** is the primary unique identifier — no two users can have the same email
- **Phone numbers** are not unique — multiple users can share the same phone
- **External ID** is an optional unique identifier from another system

### How create_or_update works

**Matching (in order):**

1. `external_id` — checked first
2. `email` — checked only if `external_id` does not match

Phone is not used for matching.

**What happens on match:**

| Matched by    | What gets updated              |
| ------------- | ------------------------------ |
| `external_id` | Email is **added** to the user |
| `email`       | External ID is **overwritten** |

Zendesk users can have multiple emails but only one `external_id`.

### Phone numbers and SMS routing

When a phone number is added to a Zendesk user:

- If the phone is **new** (not used by anyone else), it becomes a "direct line" and SMS routing works
- If the phone **already exists** on another user, it becomes "shared" and SMS routing does not work

For SMS routing to work reliably, phone numbers should be unique to one user.

## Configuration

The app uses OAuth 2.0 authentication and requires the following configuration:

**Required configuration:**
- `subdomain` (string) — Your Zendesk subdomain (e.g., `"your-company"` for `your-company.zendesk.com`)
- `client_id` (string) — Your Zendesk OAuth client ID

**OAuth credentials:**
- `client_secret` — Zendesk OAuth client secret
- `access_token` — Generated during OAuth authorization
- `refresh_token` — Generated during OAuth authorization

> **Note:** When installed in a Gladly environment, OAuth credentials (`client_secret`, `access_token`, `refresh_token`) are managed through the `appcfg apps oauth` flow and should not be manually configured. For local testing with `appcfg run oauth`, you must provide your own Zendesk OAuth client credentials — see [Test locally](#test-locally).

**Example configuration JSON:**
```json
{
  "integration": {
    "configuration": {
      "subdomain": "your-zendesk-subdomain",
      "client_id": "your-oauth-client-id"
    }
  }
}
```

## Setup and installation

### Prerequisites

- A Zendesk account with admin access
- The [appcfg CLI](https://github.com/gladly/app-platform-appcfg-cli) installed and required environment variables set
- Gladly Administrator access

### Installation steps

1. **Create a Zendesk OAuth client**

   In your Zendesk admin panel, create an OAuth client and note the `client_id` and `client_secret`. See [Zendesk OAuth documentation](https://developer.zendesk.com/documentation/ticketing/working-with-oauth/) for details.

2. **Configure the app**

   ```bash
   appcfg apps config create "gladly.com/zendesk/v1.0.0" \
     --name "Zendesk <your company>" \
     --config '{"subdomain": "<your_zendesk_subdomain>", "client_id": "<your_oauth_client_id>"}' \
     --secrets '{"client_secret": "<your_oauth_client_secret>"}'
   ```

3. **Retrieve the configuration ID**

   ```bash
   appcfg apps config list --identifier "gladly.com/zendesk/v1.0.0"
   ```

   Note the `CONFIG ID` from the output.

4. **Run the OAuth authorization flow**

   Complete the OAuth authorization against your Gladly environment:

   ```bash
   appcfg apps oauth <config-id>
   ```

   This will:
   - Open a browser window for Zendesk OAuth authorization
   - Prompt you to log in with your Zendesk account and authorize the app
   - Store the OAuth credentials in your app configuration

5. **Activate the app**

   ```bash
   appcfg apps config <config-id> --activate
   ```

## Idempotency

The `createTicket` action automatically uses the App Platform `correlationId` as the `Idempotency-Key` header, ensuring retries do not create duplicate tickets. The key is valid for two hours in Zendesk.

## Error handling

The app handles various error scenarios:

- **Validation errors** (400/422) — detailed validation messages
- **Authentication errors** (401/403) — authentication failure messages
- **Conflict errors** (409) — duplicate ticket messages
- **Rate limiting** (429) — rate limit exceeded messages
- **Server errors** (5xx) — generic error messages

## Testing and troubleshooting

### Run tests

- **Validate app configuration:**
  ```bash
  make validate
  ```
- **Run all unit tests:**
  ```bash
  make test
  ```

### Test locally

To test the app locally, you need your own Zendesk OAuth client credentials. Local testing uses `appcfg run oauth` (which runs the OAuth flow against a local redirect), whereas installation in a Gladly environment uses `appcfg apps oauth` (which stores credentials in your Gladly app configuration).

> **Important:** The `_run_/` directories throughout the app contain placeholder configuration for local testing. Before running locally, update all `_run_/` configuration files with your own Zendesk instance details (`subdomain`, `username`, `client_id`, etc.). This includes files under `app/authentication/oauth/_run_/`, `app/actions/_run_/`, and any action-specific `_run_/` directories.

1. **Set up environment variables**

   ```bash
   cp .env.example .env
   ```

   Edit `.env` and set your `ZENDESK_CLIENT_SECRET`.

2. **Obtain an OAuth access token**

   Run the local OAuth flow:

   ```bash
   make run-oauth
   ```

   This will:
   - Open a browser window for Zendesk OAuth authorization
   - Prompt you to log in with your Zendesk account and authorize the app
   - Display the `access_token` and `refresh_token` in the browser output

3. **Add the access token to your environment**

   Copy the `access_token` from the browser output to your `.env` file:

   ```env
   ZENDESK_ACCESS_TOKEN=<access_token from browser>
   ```

4. **Run actions locally**

   ```bash
   # Create a ticket using an existing Zendesk user ID
   make createTicket-action-graphql REQUESTER_ID=<zendesk_user_id>

   # Add a comment to an existing ticket
   make addComment-action-graphql TICKET_ID=<zendesk_ticket_id>

   # Search for users by phone number
   make searchUsersByPhone-action-graphql PHONE=+15551234567

   # Create a new end user
   make createEndUser-action-graphql USER='{"name": "John Doe", "email": "john@example.com"}'
   ```

## Maintainer information

This integration is built and maintained by Gladly.
