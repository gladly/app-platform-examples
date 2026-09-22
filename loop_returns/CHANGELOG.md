# Changelog

All notable changes to the Loop Returns app will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.6.2] - 2026-07-30

### Added
- `get_draft_totals` action — reads a draft return's totals (`amountDue`, `exchangeFee`, `isDifferenceCovered`) so a workflow can detect an exchange handling fee or price difference after `finalize_items` and avoid an opaque submit failure. The exchange sample now calls it and hands off when the difference is not covered.

### Fixed
- 422 responses now surface Loop's `errors[].code` (e.g. `draft-return-not-actionable`, `action-validation-failed`) instead of collapsing to a generic message; added a nullable `code` to the error type. Applied to the draft-return/returns actions; `create_allowlist_item` is unchanged (different error shape).
- `add_exchange_item` now returns the real `exchangeVariantId` by reading the exchange item from `draft_return.cart_items` (matched to the returning item), instead of a non-existent top-level `exchange_item` object — it previously always returned `"0"`.

### Changed
- Exchange workflow sample no longer echoes numeric return-reason ids to customers.

## [1.6.1] - 2026-07-29

### Fixed
- Exchange variant catalog (`exchangeOptions`) is now returned by `get_draft_return` instead of `check_eligibility`, and is keyed per returning item (`returningItemId`). Loop only populates the catalog after a returning item's type is set to `exchange`, and returns it as a map keyed by returning-item id — so the 1.6.0 placement on `check_eligibility` (at draft creation) always came back empty. `check_eligibility` no longer populates `exchangeOptions`; the field is retained on its result type (deprecated, always empty) for backward-compatible in-place upgrade.

## [1.6.0] - 2026-07-28

### Added
- `check_eligibility` returns `exchangeOptions` — the exchange variant catalog for the order (per-product size/color options plus each variant's id, option values, price, and in-stock flag), for guided line-item variant exchanges. (Superseded by 1.6.1, which moves this to `get_draft_return`.)

## [1.5.0] - 2026-07-16

### Added
- Return-creation workflow. `check_eligibility` checks return eligibility for an order and creates a draft return in the same call (returning the draft return ID, eligible/ineligible order line items, and available return methods), followed by the draft submission chain: `add_return_item`, `set_return_reason`, `set_return_type`, `set_credit_type` (refund/credit returns), `add_exchange_item` (same-product variant-swap exchanges, with the replacement variant resolved in Shopify), `finalize_items`, `select_return_method`, and `submit_return`.
- `get_draft_return` action to inspect a draft return's current state and next legal steps, and `cancel_draft_return` to abandon an unsubmitted draft.
- `get_return_details` action to fetch full details of an existing return by return ID, order ID, or Shopify order name.

### Notes
- The return-creation actions are powered by Loop's Return Create (Draft Returns) API, which is in beta and not enabled by default — contact your Loop representative to have it activated for your shop. No additional app configuration is required beyond the existing API token.

## [1.4.0] - 2026-04-22

### Added
- Admin UI configuration form (`ui/admin/form.json`).

## [1.3.0] - 2025-07-29

### Added
- Initial app release in source control: card-based UI, `create_allowlist_item` and `generate_return_link` actions, data pulls, and authentication setup.

### Changed
- Addressed PR feedback prior to release.

### Removed
- Extra/unused types.
- Status handling from data pulls (2025-07-30).

### Fixed
- Card UI fixes and data updates.
- Comment fixes (2025-08-05).
- Documentation fixups (2025-08-22).

> History prior to 1.3.0 predates this app's presence in source control.
