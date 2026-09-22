# Changelog

All notable changes to the Yotpo app will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.2.6] - 2026-07-31

### Fixed
- Customer data pull no longer errors for shoppers whose Yotpo record has no `account_updated_at` timestamp (typically review-only shoppers without a commerce account, included since 2.2.4). The record's freshness timestamp is now omitted instead of failing the customer's whole Yotpo refresh, which previously blanked the customer, reviews, and loyalty cards for these shoppers.
- Customer data pull no longer errors for Gladly customers with no email address or mobile phone number. The pull now simply returns no data (hiding the Yotpo cards) instead of raising a "No email addresses or mobile phone numbers available for customer lookup" error on every profile view.

## [2.2.5] - 2026-07-09

### Fixed
- Loyalty data pull no longer errors out the whole Yotpo profile when Yotpo returns a `400` for a customer with no loyalty account. This completes the 2.2.4 fix, which only covered the `404` variant of the same "customer not enrolled in loyalty" case; Yotpo returns a `400` for some of these shoppers instead. The loyalty card is now simply hidden for them, rather than surfacing an error that blanks the customer and reviews cards too.

## [2.2.4] - 2026-07-08

### Fixed
- Loyalty data pull no longer errors out the whole Yotpo profile when a customer is not enrolled in the loyalty program. Yotpo returns a 404 ("Could not find customer") for these shoppers; the loyalty card is now simply hidden instead of surfacing an `HTTP_STATUS_404` error across the app.
- Customer profile (and the reviews that depend on it) no longer errors when a matched Yotpo customer has no `external_id` — which happens for shoppers who exist only via reviews and aren't tied to a commerce/loyalty account. The customer's email is used as the identifier in that case (matching how loyalty and reviews are already linked), falling back to phone number; customers with no usable identifier are skipped instead of failing the whole pull.
- When no Yotpo customer matches the contact — every returned customer's name mismatches, or none has a usable identifier — the customer card is now simply hidden rather than erroring the entire Yotpo profile (which previously also blanked the loyalty and reviews cards).

## [2.2.1] - 2026-06-12

### Fixed
- Normalized inconsistent datetime values returned by Yotpo. Customer and review timestamps are now parsed across the formats Yotpo sends (with or without a timezone offset, and date-only) and converted to UTC ISO 8601; values that can't be parsed are set to null.
- Loyalty timestamps that already included a timezone offset were being corrupted by an appended `Z` (e.g. `2022-02-01T13:05:57.071+02:00Z`); these are now passed through unchanged.
- Customer, reviews, and loyalty data pulls now fail with a clear error when `store_id` or `access_token` is not configured, instead of issuing a malformed request.

## [2.2.0] - 2026-04-22

### Added
- Admin UI configuration form (`ui/admin/form.json`).

### Changed
- Updated form structure, label, hint text, and documentation.

> The same-day session also touched intermediate versions 2.1.1 and 2.1.0 before settling at 2.2.0.

## [2.0.1] - 2026-04-02

### Fixed
- `next_points_expire_on` converted to full ISO 8601 datetime (#358).

## [2.0.0] - 2026-02-19

### Added
- Yotpo card improvements based on Yotpo's recommendations (#331).
- GitHub Actions workflow for app validation and testing (#334), 2026-02-16.

## [1.0.1] - 2025-12-23

### Fixed
- `access_token` flow correctly sets `redirect_url` (#251).

## [1.0.0] - 2025-07-16

### Added
- Initial Yotpo Reviews & Loyalty app: customer card, reviews card, customer/reviews/product/order data pulls, OAuth authentication.
- Subsequent feature work and release-process fixes shipped under 1.0.0 through October 2025: customer card additions (#220), release-process fixes (#242), mobile-number checks for data-pull viability.

> During initial development the version briefly went `1.0.0-dev.1` (2025-06-27) and `1.0.2` (2025-07-15) before being renumbered back to 1.0.0 on 2025-07-16.
