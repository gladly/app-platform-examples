# Changelog

All notable changes to the Recurly app will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.2.0] - 2026-08-10

### Added
- `terminate_subscription` action — immediately expires a subscription, with an optional refund of `full` (default), `partial`, or `none`. Unlike cancellation, a terminated subscription cannot be reactivated.
- Subscription display card (`ui/templates/recurly-subscriptions`) shown in Sidekick — lists each of a customer's subscriptions with plan, status, renewal/lifecycle dates, and a charge breakdown (add-ons, discounts, pending changes).
- Agent forms to cancel, resume, and reactivate a subscription from Sidekick (`ui/forms/cancel-subscription`, `ui/forms/resume-subscription`, `ui/forms/reactivate-subscription`). Each presents a dropdown filtered to only the eligible subscriptions for that operation.
- `expiration_reason` field on the subscription returned by actions, indicating why a subscription expired (e.g. `terminated`).

## [1.1.0] - 2026-04-21

### Added
- Admin UI configuration form (`ui/admin/form.json`).

### Changed
- Updated hint text (2026-04-22).

## [1.0.0] - 2025-01-24

### Added
- Initial Recurly app: header-based authentication; account data pull; subscription actions (`cancel_subscription`, `lookup_subscription_by_id`, `pause_subscription`, `reactivate_subscription`, `resume_subscription`).
