# Changelog

All notable changes to the Ordergroove app will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.3.5] - 2026-09-16

> Released as 2.3.5. Versions 2.3.0 through 2.3.4 were built and installed for UAT
> testing but never released; everything found during that testing is folded into
> the entry below.

### Added
- **Customer profile card.** The app has pulled customer, subscription, order, item and product data since 2.x and rendered none of it, so an agent saw nothing unless their org hand-built a card. `ui/templates/ordergroove-subscriptions` now renders subscriptions with status, cadence, quantity, price, prepaid state, product detail, order history with totals, and line items.
- **The card is built for a narrow agent pane.** Everything is collapsed at rest: a subscription is three lines (product, status, cadence) and an order is one (`summaryLabel` - date and status - plus the total), each opening on click. Only the four most recent orders render, plus every actionable order regardless of position. Orders are sorted by placement date in the data pull before that cap is applied, because Ordergroove returns them in no reliable order and the endpoint accepts no ordering parameter - without the sort, "four most recent" meant four arbitrary rows. The full order list stays in the payload for actions and order pickers. New display fields: `placeLabel`, `summaryLabel`, `displayRank`, `isRecent`.
- **Nine agent actions.** `changeSubscriptionFrequency`, `changeSubscriptionQuantity`, `changeNextOrderDate`, `changePrepaidRenewalBehavior`, `cancelOrder`, `sendOrderNow`, `changeItemQuantity`, `deleteOrderItem`, and `issueOneTimeDiscount`.
- **Twelve agent forms**, including the first forms for the already-shipped `cancelSubscription`, `reactivateSubscription` and `skipSubscription` — `skipSubscription` had no form because it needs an order id and `orders` was not a field on `type Query` until this release.
- `orders` added to `type Query`, backed by the existing orders pull, so forms can bind an order picker.
- `Order.sub_total`, plus card-safe camelCase twins and derived display fields (`statusLabel`, `cadenceLabel`, `quantityLabel`, `triesLabel`, `isActionable`, `isPrepaid`, `prepaidOrdersRemaining`, `renewalBehaviorLabel`) across every data type.
- `Subscription.fallbackTitleLabel` and `Subscription.prepaidLabel`. The card expression language's `+` is numeric, so any display string has to be assembled in the data pull rather than in the template; these two carry the finished text the card would otherwise have had to concatenate.
- Admin configuration for one-time discounts: an off-by-default toggle plus a maximum amount and maximum percentage. An unset maximum refuses that kind of discount; it never means unlimited.
- The one-time discount action confirms with a `confirmed` checkbox that defaults to off, matching the confirmation idiom used by the other money-moving actions in this suite, rather than asking the agent to type a word.

### Fixed
- **Order status rendered blank in several states that generate contacts.** Only 7 of Ordergroove's 16 documented status codes were mapped; everything else became `null`. `CANCELLED`, `MERGED`, `PENDING_VERIFICATION` and the four failure states all showed nothing. All 16 are now mapped in every template that maps status, and codes 19 and 20 have been added to the `OrderStatus` enum, which never declared them.
- **The products pull dropped eleven declared fields**, including `image_url`, `detail_url`, `product_type` and `prepaid_eligible` — the last two decide whether an action is legal on a row. It also interpolated values straight into JSON, so a null rendered as the literal `<no value>`: wrong data when quoted, invalid JSON when not.
- `Order.sub_total` was never pulled, so no pre-tax line could be shown.
- **Order history rendered out of sequence on the card.** An order placed for a future date sat below an older one that had changed more recently, so the upcoming delivery was not the first row an agent saw. Sorting the data pull's output did not fix it: the platform requests orders as "newest first" and ranks them by the timestamp the app reports for each record, which was the vendor's last-modified date. That timestamp is now the later of the placement date and the last-modified date, so an upcoming order sorts to the top on its delivery date while settled orders still sort on when they last changed.
- **Placing an order early and cancelling an order work; an earlier note here said otherwise.** The platform request log shows `sendOrderNow` returning 200 and the order moving to `SEND_NOW` on the agent's click, and `cancelOrder` cancelling as expected. Neither needs a request body. In one test the success confirmation did not appear in the timeline; the request log holds only data and action calls, so the cause of that is not visible from the app and is being watched rather than claimed.
- **Known issue (platform): forms that bind `orders` or `items` can fail to generate for one customer profile.** The platform's child resolution for `Item.productDetail` fails with "could not retrieve external data for child IDs" on a few item records only, while the same product resolves for every older item in the same second. The card is unaffected because its nested result did not include the affected items. **Reinstalling the app does not clear it** — the bad state is held per customer profile, not per app install. Deleting and recreating the Gladly customer profile does clear it, confirmed on a UAT profile 2026-09-16. Escalated to the platform team with correlation IDs. Unrelated hardening in this release: products now carry an `external_updated_at` like every other record type, so they refresh alongside the items that reference them — products were previously the one type that never re-ingested once stored. That was not the fix for this issue.
- **Removing or changing an item on an order that had just moved on reported only `failed: 500`.** Ordergroove returns a 500 with an empty body when an order is no longer open for changes, and orders churn constantly — one observed live went from upcoming to rejected within minutes of the card loading, because whether an order is actionable is decided when the data is pulled, not when the agent clicks. The order and item actions now explain that the order has moved on and to refresh the profile, instead of surfacing a bare status code.
- **Payment-failure reasons rendered as a raw JSON blob.** Ordergroove returns `rejected_message` as a JSON-encoded string rather than prose, so the card showed the agent `{"code": "500", "message": "Expiration Date is in the past."}` instead of the sentence inside it — on the exact card an agent opens during a billing-failure contact. The readable message is now extracted in the data pull, independent of the vendor's key order, with anything that is not a JSON object carrying a message passed through unchanged.
- `getCancellationReasons` emitted merchant reasons under a key named `cancel_reason` while the GraphQL type declared `code`, so `code` could never resolve — and it is declared non-null, which turns an unresolvable field into a hard failure rather than a blank. The value was also interpolated unquoted, so a non-numeric reason produced invalid JSON. Two correctly typed fields replace it: `reasonCode` (nullable `Int`, safe to select on every row) and `cancelReason` (the raw vendor string). `code` keeps its shipped non-null type, because narrowing it is a breaking schema change the platform rejects on upgrade, and is now populated whenever the vendor value is numeric — more than it ever managed before. It is deprecated and will be removed in the next major.
- The Makefile's live-run targets referenced `-q customer`, `-q subscription` and `-q order`, none of which are fields on `type Query`, and passed `subscriptionId` to `getShippingAddress`, which takes `addressId`.

### Changed
- **Bounded the per-profile request fan-out.** The items pull issued one request per order against the customer's entire order history — roughly 500 sequential requests for a long-tenured subscriber with several subscriptions. It now fetches every order an agent can still act on, plus the six most recent completed orders **per subscription**, capped at 30 historical requests overall. Orders in a terminal failure state (`REJECTED`, `RESPONSE_PROCESSING_ERROR`, `EXCEPTION_DURING_PLACEMENT_PREPARATION`) count as history rather than as actionable, so a subscriber whose payments have failed every cycle for years no longer produces an unbounded fan-out — previously the worst case fell on exactly the customers most likely to contact support. A typical five-subscription profile now issues roughly 40 item requests instead of about 500.

> Ordergroove's orders endpoint exposes no date or status filter, so this is a mitigation rather than a fix. The order pull itself still returns full history.

## [2.2.1] - 2026-06-16

### Fixed
- List requests now ask Ordergroove for `page_size=100` (its maximum) instead of relying on the default page size of 10, so larger result sets are no longer silently truncated to the first 10 records. This covers the subscription, order, item, and product data pulls, plus the `getSubscriptionsByCustomerId` and `getOrderItems` actions. Previously a customer with more than 10 subscriptions (or an order with more than 10 items) returned an incomplete list, and valid products beyond the first page were returned as discontinued stubs with no name.

> Ordergroove's REST list endpoints cap a single response at 100 records, and the App Platform does not follow `next` pagination links, so pulls exceeding 100 records may still be truncated.

## [2.2.0] - 2026-05-21

### Added
- Backfill stub products for orphaned `external_product_id`s in the products data pull. References that don't resolve to a live product now render as stub products flagged via `extra_data` and marked discontinued (`live: false`) instead of being dropped.

## [2.1.4] - 2026-04-21

### Added
- Admin UI configuration form (`ui/admin/form.json`).

### Changed
- Updated hint text (2026-04-22).

> Versions 2.1.0 through 2.1.3 were not released; the manifest jumped from 2.0.4 to 2.1.4.

## [2.0.4] - 2026-01-30

### Fixed
- Null update-datetime handling for entities.

## [2.0.3] - 2025-12-05

### Fixed
- Enhanced conditional checks for external data in request URL templates.

### Added
- Test fixtures for nil-key scenarios.

## [2.0.2] - 2025-12-05

### Fixed
- Gracefully handle missing parent data in nested resolvers.

### Changed
- Simplified templates per Copilot review.

### Added
- Additional test coverage.

## [2.0.1] - 2025-11-13

### Fixed
- Product fetching and product data-type dependencies.

## [2.0.0] - 2025-10-16

### Added
- Initial app release in source control: 8 subscription-management actions (`cancelSubscription`, `getCancellationReasons`, `getOrderItems`, `getShippingAddress`, `getSubscriptionById`, `getSubscriptionsByCustomerId`, `reactivateSubscription`, `skipSubscription`); customer/order/subscription data pulls; authentication; UI templates; public documentation.

### Changed
- Release fixes and documentation polish.

> History prior to 2.0.0 predates this app's presence in source control.
