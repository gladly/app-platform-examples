# Changelog

All notable changes to the Zendesk app for Gladly are documented in this file.

## v1.0.0

Initial release.

### Mutations

- **createOrUpdateCustomer** — Create or update a Zendesk user using the Gladly Customer ID as `external_id` for idempotent resolution
- **createTicket** — Create a new Zendesk ticket with support for requester lookup, priority, status, type, tags, and channel
- **addComment** — Add a public or internal comment to an existing Zendesk ticket
- **createEndUser** — Create a new Zendesk end-user linked to a Gladly Customer ID

### Queries

- **searchUsersByPhone** — Search for Zendesk users by exact phone number match

### Authentication

- OAuth 2.0 authorization code flow with automatic token refresh
- Idempotency support via `correlationId` as the `Idempotency-Key` header on ticket creation
