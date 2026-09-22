# Changelog

All notable changes to the Gladly APIs app will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.6.0] - 2026-08-26

### Added
- `agents` action — returns the list of agents (id, name, email address).
- `update_customer` action — patches a customer profile's name, image, address, emails and phones.

## [1.4.0] - 2026-04-22

### Added
- Admin UI configuration form (`ui/admin/form.json`).

## [1.3.0] - 2026-04-07

### Added
- `send_email` action.
- `toJson` template helper applied across relevant fields.

### Changed
- Hardened action templates to handle older actions without templates.

### Removed
- `create_customer_activity_item` action (superseded by `_for_email` and `_for_phone` variants).

## [1.2.0] - 2025-06-17

### Added
- `create_customer_activity_item_for_email` and `create_customer_activity_item_for_phone` actions.
- 400 and 409 response handling and test cases for `create_customer_activity_item`.
- `_run_` helpers for `create_customer_activity_item`.

### Changed
- Standardized response types across the Gladly API actions.
- Switched app username to `integrations@gladly.com`.

### Removed
- Unused file cleanup.

## [1.1.0] - 2025-05-20

### Added
- Build/test `Makefile` for `appcfg validate`/`test`/`build` workflows.
- Actions schema (`actions/actions_schema.graphql`) and supporting test fixtures.

## [1.0.0] - 2024-06-05

### Added
- Initial app for exposing Gladly APIs.
