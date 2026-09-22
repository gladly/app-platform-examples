# Changelog

All notable changes to the Shopify App will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [5.12.0] - 2026-08-18

### Added

- **Shipping, import duties, and the amounts received, refunded and outstanding on orders**, along with `taxesIncluded` and whether an order can still be refunded. The pull previously carried subtotal, discounts, tax and total only, so the figures did not reconcile and a partly refunded order was indistinguishable from a cheap one.
- **Both currencies on every money amount** — the shop's, and the one the Customer was billed in.
- **The order's overall return state**, so a return in progress is visible without opening the order.
- **Per-line unit counts** — units still on the order, and units still to ship. The existing `quantity` counts units since refunded or removed.
- **The selling plan on each order line**, identifying a subscription purchase. Empty on a one-time purchase.
- **The order's origin** — the app it was placed through, and Shopify's raw source value.
- **The full money history on `orderByIdentifier`** — the order as placed, its value after returns and edits, the split between order-level and line-level discounts, refunded shipping, uncaptured authorisation, per-line refund amounts, and the discrepancy between a refund issued and the amount Shopify suggested. Refund lines previously carried quantities with no money.
- **Shopify's customer segmentation and predicted spend tier**, plus display name and locale, on the Customer.
- **A truncation flag on metafields**, for the Customer and for each order. A capped list previously looked identical to a complete one.
- **`processedAt` on `orderByIdentifier`**, which differs from `createdAt` on an imported or backdated order.

### Fixed

- **Order money descriptions that stated the wrong thing.** The subtotal claimed to exclude tax where a tax-inclusive market puts it inside, and the order total read as fixed when an order edit moves it. Gladly AI reasons from these descriptions, so both were producing wrong answers. All money descriptions are now written against real orders, and the order list and order details describe the same field identically.

### Changed

- **All three data pulls and `orderByIdentifier` moved to Shopify Admin API `2026-07`** — `customer` was `2025-04`, `metafields` `2025-10`, `orders` and `orderByIdentifier` `2026-04`. `2025-04` is out of support and `2025-10` becomes inaccessible on 2026-10-16. Every field these queries read behaves identically on `2026-07`.
- **The data pull descriptions now state when to use each pull.** They also record that the order list is sorted by when each order was last *updated* rather than placed, and that an empty Customer result means a guest checkout rather than a new Customer.
- **The customer data pull reads email and phone from Shopify's non-deprecated fields.** The data Gladly receives is unchanged.
- **`shopify_order` `4.0` → `4.1` and `shopify_customer` `3.1` → `3.2`.** No existing field changed, so Rules and Customer Profile cards built against the earlier versions keep working.

### Deprecated

Markers only — every field below still returns what it always did. They flag what the next major version will drop.

- **`MailingAddress.name`** — not populated. Use `firstName` and `lastName`.
- **`Customer.lastOrder`** — never populated. Use `orders`.
- **`Customer.lifetimeDuration`** — compute from `createdAt`.

## [5.11.3] - 2026-08-18

### Changed

- **`jobStatus` moved to Shopify Admin API `2026-07`** (was `2025-04`, which is out of support; sc-254742), the current stable version. The cancellation and refund detail it returns is unchanged — every field the action reads behaves identically on both versions.

## [5.11.2] - 2026-08-18

### Fixed

- **Trimmed the `customer` and `metafields` data pulls** to stop requesting fields Gladly never stored (the customer avatar `image`, the deprecated `addresses` list, `defaultAddress.formattedArea`, and the per-metafield `owner` block, which repeated the id of the customer or order the metafield already hangs off). Smaller Shopify queries, no change to the data Gladly receives.

## [5.11.1] - 2026-08-18

### Changed

- **`lookupOrdersByEmail` moved to Shopify Admin API `2026-07`** (was `2026-04`), the current stable version. The orders it returns are unchanged — every field the action reads behaves identically on both versions.

### Fixed

- **`lookupOrdersByEmail` accepts email addresses containing a hyphen** (sc-247536). Any address with a `-` before the `@` — Apple "Hide My Email" relay addresses, and hyphenated addresses like `jane-smith@gmail.com` — was rejected with "Invalid email address format provided" and returned no orders. Apostrophes, `+` tags and non-ASCII addresses are now accepted too, and the address is trimmed of surrounding whitespace and lowercased before searching. An address is still rejected when it is empty, has no `@` or more than one, contains whitespace, or has a domain with no dot.
- **The email is passed to Shopify as a GraphQL variable** instead of being built into the query text, so the request always has the same shape regardless of the address searched for.

## [5.11.0] - 2026-08-18

### Added

- **`customAttributes` on `orderByIdentifier` line items** — order line items now carry their custom attributes, the key-value properties that store additional information about a line, such as custom features or special requests. Returned as stored, and absent when a line has none.

## [5.10.0] - 2026-08-18

### Added

- **`getFilesByIds` action** (`get_files_by_ids`, API `2026-07`) — resolves Shopify file GIDs to the files they point at. An image returns its URL, pixel dimensions and alt text. A document such as a PDF returns its URL, MIME type and size in bytes. Both carry the file's type, processing status and timestamps. Accepts up to 25 GIDs of mixed type per call; a GID that doesn't resolve to a file is reported as `not_found` and the call still succeeds.

## [5.9.0] - Released

### ⚠️ BREAKING CHANGE

- **Decluttered the slim order reads and the `orders` data pull** (`lookUpOrderByOrderNum`, `lookUpOrdersByEmail`, and the `shopify_order` data pull; sc-247884). On the shared `LineItem`: removed the long-dead `variantId`/`variantTitle` scalars in favor of `variant` (`ProductVariantRef {id, title}`), and replaced the heavy `product` embed (full catalog — `variants`/`collections`/`options`/`status`/`tags`/…) with a lean `product` (`ProductRef {id, title}`). On the slim `Order`: removed `transactions`, `billingAddress`, `fulfillments`, `fulfillable`, and `fulfillmentsCount`, and trimmed `ShippingLine` to `title` only — both lookups now return the same slim `Order` (the email lookup's prior over-fetch made large result sets unreadable). Removed the now-unused `Fulfillment` cluster, the catalog types (`Variant`/`Collection`/`ProductOption`/`ProductStatus`), and — on the data pull only — the `OrderTransaction` cluster.
- **`orders` data pull dataType `shopify_order` bumped `3.3` → `4.0`** for the line-item and `Order` reshape above. Rules and the order card that referenced the removed nested `product` fields or removed order-level fields must be updated.

### Fixed

- **`lookUpOrdersByEmail` sorts by creation date** (`CREATED_AT`, was `UPDATED_AT`): a recently-touched old order no longer displaces the newest orders out of a capped result. The limit description was corrected to reflect the integration's `ordersLimit` (default 10).
- **Line items populate `giftCard`** (mapped from Shopify's `isGiftCard`), and custom (off-catalog) line items are kept with `price`/`totalDiscount` populated.
- **Error responses are JSON-safe** across the three reads (`toJson`-wrapped messages, guarded `extensions.code`), so a quote/newline in a Shopify error message — or a missing `extensions` — no longer yields invalid JSON.

## [5.8.0] - Released

### ⚠️ BREAKING CHANGE

- **Removed the `refundLineItem` action** — superseded by `refundOrder`.
  - It was broken: pinned to out-of-support `2025-04`, missing the now-mandatory `refundCreate @idempotent` directive, never used.
  - Also removed its now-unused types: `RefundLineItemResult`, `RefundResult`, `RefundLineItem`.
- **Reshaped `suggestedRefund`** into a whole-order / partial preview; pinned `2025-04 → 2026-04`.
  - Inputs: `orderId`, optional `refundLineItems` (new `RefundLineItemInput`), `suggestFullRefund` (default `true` = whole order), `refundShipping` — replacing the old single required `lineItemId` / `quantity`.
  - Result mirrors Shopify in shop+presentment `MoneySet`: `amountSet`, `maximumRefundableSet`, `subtotalSet`, `totalTaxSet`, `shipping` (`ShippingRefund`), `refundLineItems` (new lean `SuggestedRefundLineItem`).
  - `suggestedTransactions` is now a **list** of a slimmed `SuggestedOrderTransaction` (`parentTransaction`, `gateway`, `formattedGateway`, `accountNumber`, `amountSet`) — fixes the prior single-object typing that dropped multi-tender data; adds masked `accountNumber` / `formattedGateway`.
  - Removed unused `Duty` / `RefundDuty` types — duty refunds are out of scope for v5.

### Added

- **`refundOrder` action** — whole-order or partial refund (`refundCreate`, `2026-04`).
  - Inputs: `orderId`, `currencyCode` (presentment), `refundLineItems`, `transactions` (both copied from a `suggestedRefund` preview), `refundShipping`, optional `note` / `notify`.
  - Idempotent per execution: the request `correlationId` is the mandatory `@idempotent(key:)`, so a retried execution replays rather than duplicating — no caller-supplied key.
  - Refunded items are not restocked (`NO_RESTOCK`). Returns `OrderRefundResult`.
- **Store-credit refunds** — new `RefundMethodAllocation` enum (`ORIGINAL_PAYMENT_METHODS` | `STORE_CREDIT`).
  - `suggestedRefund` gains `refundMethodAllocation` (default `ORIGINAL_PAYMENT_METHODS`) so the preview's `amountSet` reflects the allocation.
  - `refundOrder` gains `refundMethod` (default `ORIGINAL_PAYMENT_METHODS`), `storeCreditAmount` (presentment), optional `storeCreditExpiresAt`; `transactions` becomes optional.
  - With `STORE_CREDIT`, the refund issues via `refundMethods.storeCreditRefund` (empty `transactions`) instead of to the original payment.
  - Store credit requires the order to have a customer; not supported for B2B orders.

### Changed

- **`refundShipping` is now a required `Boolean!`** on both `suggestedRefund` and `refundOrder` (was `Boolean = true`).
  - The silent `true` default meant a partial refund that omitted it quietly refunded all shipping on top of the line.
  - All-or-nothing (true = all shipping, false = none); partial shipping amounts aren't supported.
  - Its value on `refundOrder` must match the one previewed with `suggestedRefund`.

## [5.7.0] - Released

### Added

- **`orderByIdentifier` action**: deep-dive order read by Shopify Order GID, returning the `OrderDetail` shape — order/financial/fulfillment status, money totals, addresses, a flat line-item list, and the fulfillment/return/refund/fulfillment-order blocks keyed back to lines by `lineItemId`.
- **`currentTotalDutiesSet` on `OrderDetail`**: the current duties owed on the order (import/customs fees), as a single current-state `MoneySet` mirroring the other order totals. Null unless the order is international / subject to DDP. Informational only — duty refunding is intentionally not supported, so no per-line `Duty` data is exposed.
- **`purchasingEntity` on `OrderDetail`**: who the order was placed for. For a D2C order, `customer` is set — its `id` is the `gid://shopify/Customer/...` usable directly by `createDraftOrderForCustomer` and store-credit refunds, so a single order read yields the customer for downstream actions. For a B2B order, `company` is set instead (company/contact/location). Null for guest / unattributed orders. Reuses the existing `PurchasingEntity` type (the shared `customer`/`company` field descriptions were generalized so the type reads correctly for orders as well as draft orders).
- **Order origin & delivery signals on `OrderDetail`** (sc-247765): `salesChannelName` (the sales channel, e.g. "Online Store") and `sourceAppName` (the app that created the order, e.g. "Point of Sale", "Draft Orders") — kept as two separate fields (never coalesced) so an app/admin/draft-created order is distinguishable from a real storefront one (`salesChannelName` is null for the former, `sourceAppName` carries the origin). `deliveryMethodName` (the human delivery method the customer chose, from the order's shipping-line title — e.g. "International Shipping" or a pickup location name). And on each `fulfillmentOrders[]`, a `deliveryMethod` (`OrderDetailDeliveryMethod`) with `methodType` (`DeliveryMethodType` — SHIPPING / PICK_UP / PICKUP_POINT / LOCAL / RETAIL / NONE; the robust ship-vs-pickup signal) plus the estimated `minDeliveryDateTime` / `maxDeliveryDateTime` window (a checkout-time snapshot; null for admin/API/manual/pickup orders).
- **`estimatedDeliveryAt` on `OrderDetail` fulfillments** (sc-247765): the carrier's current estimated arrival for each shipment (`fulfillments[].estimatedDeliveryAt`). This is the CARRIER estimate ("what the carrier now says"), distinct from the checkout-promise window on `fulfillmentOrders[].deliveryMethod` (`minDeliveryDateTime`/`maxDeliveryDateTime`, "what was promised at checkout") — different sources, both may be present. Often null (manual fulfillments, or carriers that don't provide an estimate).
- **Truncation signals on `OrderDetail`**: `hasMoreLineItems`, `hasMoreFulfillmentOrders`, `hasMoreReturns` (Boolean) flag when a cursor-paginated block has more than the returned page (`lineItems` first 50; `fulfillmentOrders` / `returns` first 20), so the refund/return workflows don't silently miss state past the cap. The plain-list `fulfillments` and `refunds` blocks are now fetched uncapped (the full list), and per-refund `transactions` fetch up to 50.
- **Variant selected options & product collections on `OrderDetail` line items** (sc-247884): each catalog line's `variant` now carries `selectedOptions` (`[{ name, value }]` — e.g. Color / Size) so the exact purchased item is identifiable, and its `product` now carries `collections` (`[CollectionRef]`, the collection memberships) plus `hasMoreCollections` (Boolean, true when the product belongs to more collections than the returned page). Useful for issuing collection-scoped discounts. Custom lines (no variant/product) omit these fields.

### Changed

- **`look_up_orders_by_email` action and `orders` data pull pinned to Admin API `2026-04`** (was `2025-04`; sc-247884, [#6](.claude/issues.md)): both order reads now match the rest of the app's newest-stable pin. Queries validate clean on `2026-04` with no deprecated fields; no behavior or response-shape change.

## [5.6.0] - Released

### Added

- **`sendDraftOrderInvoice` action** — emails the customer a hosted Shopify checkout link to pay for an OPEN draft order (the paid alternative to completing a draft with no payment); moves the draft OPEN → INVOICE_SENT and returns the invoice URL + sent timestamp. Recipient resolves from the draft's email, an `email` override, or the attached customer's email. Returns `DraftOrderInvoiceSendResult` (`draftOrder: DraftOrderInvoiceRef`, `errors`, `userErrors`). Pinned to the newest stable Admin API version.
- **`lockPrices` input on `createDraftOrderForCustomer`** — when true, snapshots each product line's current catalog price (`generatePriceOverride: true`) so a quoted price holds through checkout; off by default. Use for quotes/contracts paid later (esp. via `sendDraftOrderInvoice`).

## [5.5.0] - Released

### ⚠️ BREAKING CHANGE

- **`updateDraftOrder` rebuilt as a basic partial-update action.** It now updates only the draft's contact `email`, `note`, `phone`, `poNumber`, and `shippingAddress` (a new reusable `MailingAddressInput`), and requires `customerId`. It no longer replaces the draft's line items — the `lineItems` arg is gone (dedicated line-item editing will return as its own action). Removed the legacy `DraftOrder` type (the old result shape) and `DraftOrderLineItemInput`.

### Changed

- **`updateDraftOrder` and `createDraftOrderFromOrder` warn instead of failing on a customer mismatch.** Both verify the resulting draft belongs to `customerId`; on a mismatch they now return the draft with a non-fatal `customer_mismatch` alert (new `alerts` field on the result) rather than failing. `createDraftOrderFromOrder` previously returned `draftOrder: null` with a `userError`.
- **Renamed the draft-order result type `DraftOrderV2` → `DraftOrder`.** The legacy v1 `DraftOrder` type was removed, so the `V2` suffix is no longer needed. Returned by `createDraftOrderForCustomer`, `createDraftOrderFromOrder`, and `updateDraftOrder`.

## [5.4.0] - Released

### ⚠️ BREAKING CHANGE

- **`createDraftOrder` removed; use `createDraftOrderForCustomer`.** The replacement requires `customerId` (every draft is tied to a customer) and takes split line items — it's not a drop-in rename.
- **Draft-order line items split into two inputs**: `productLineItems` (by variant ID) and `customLineItems` (ad-hoc title/price). The single `lineItems` arg is gone.

### Changed

- **`createDraftOrderFromOrder` now requires `customerId`** and verifies the created draft belongs to that customer.

## [5.3.1] - Released

### ⚠️ BREAKING CHANGE

- **`completeDraftOrder` inputs reworked**: removed the `paymentGateway` input. Added an optional `sourceName` input for sales-channel attribution of the resulting order; when not specified, the source name defaults to `gladly`.
- **`completeDraftOrder` now defaults to payment pending**: the `paymentPending` input is kept as a simple paid/pending toggle (Shopify deprecates it in favor of payment terms on the draft order itself — we use it deliberately), but it now defaults to `true`. When not specified, the resulting order's `financialStatus` starts as `PENDING` and payment is collected later (e.g. by invoice or payment terms) — a behavior change for callers that relied on completion marking the order as paid. Pass `paymentPending: false` to mark the order as paid on completion.
- **`completeDraftOrder` response reworked**: now returns a summary of the **order created from the draft** (`order`: financial/fulfillment status, purchasing entity with B2B company, shipping address, line items with discounted totals, price totals, note, payment terms) plus a minimal `draftOrder` reference (`id`, `name`, `status`) — instead of the full draft order. Callers that previously read draft-order fields off this response must switch to the new shape.

## [5.3.0] - Released

### Added

- **`getProductInventory` action**: fetch live inventory for product variants by product or variant GID (up to 25 per call, mixed). Returns a flat list of variants with `availableForSale`, `inventoryQuantity`, `sellableOnlineQuantity`, `inventoryPolicy`, and `tracked`. Quantities are totals across all locations and are null for untracked variants.

## [5.2.0] - Released

### ⚠️ BREAKING CHANGE

- **`read_companies` and `read_payment_terms` OAuth scopes** — needed by the new action for the B2B purchasing entity and the draft's payment terms; other actions and data pulls are unaffected.

### Added

- **`createDraftOrderFromOrder` action**: duplicate an existing order into a new draft order (Shopify's `draftOrderCreateFromOrder` mutation, Admin API `2026-04`) — for example, to re-order the same items for a customer. Returns the draft with line items (including per-variant stock availability), the customer or B2B purchasing entity, addresses, discounts, and totals.
  - `DraftOrderV2` exposes a `hasMoreLineItems` truncation flag, signalling when the draft order has more line items than the first 50 returned in `lineItems`.

## [5.1.0] - Released

### Added

- **`createOneTimeCustomerDiscount` action**: issue a one-time, amount-off discount code for a single customer — for example, to make good on a product issue with a few dollars off their next order.

### Fixed

- **Address `formatted` field is now nullable (`[String!]`).**

## [5.0.2] - Released

### ⚠️ BREAKING CHANGE

- **Update Shipping Address:** response slimmed to the order and its updated address only — callers reading other order fields off this response must fetch them elsewhere.
- **Update Shipping Address:** dropped 6 dead legacy fields (`country_code`, `country`, `email`, `province_code`, `province`, `state`); use `countryCodeV2`/`provinceCode`.
- **Address `countryCode` renamed to `countryCodeV2`:** Shopify deprecated the `countryCode` field on its `MailingAddress` schema, so every operation that returns an address now exposes the ISO country code as `countryCodeV2`. The value is unchanged — only the field name differs. Affected:
  - Order data pull (shipping + billing address)
  - Customer data pull (default address + saved addresses)
  - Look Up Order by Order Number (shipping + billing address)
  - Look Up Orders by Email (shipping + billing address)
  - Update Shipping Address (returned address)

### Added

- **Update Shipping Address:** result now includes the formatted address, validation status, and location coordinates.
- **Order data pull:** shipping address now includes recipient name, phone, and company (plus validation status and coordinates).

### Fixed

- **Update Shipping Address:** validation errors now name the rejected field (e.g. missing city) instead of an unreadable string.

## [5.0.1] - Released

### ⚠️ BREAKING CHANGE

- **`cancelOrder` now takes a structured `refundMethod` input instead of the `refund` boolean.** Callers that previously sent `refund: true` must now send `refundMethod: { originalPaymentMethodsRefund: true }`; to issue no refund, omit `refundMethod` entirely. Authorized (uncaptured) payments are voided on cancellation regardless of this setting. This replaces Shopify's deprecated `refund` argument on the `orderCancel` mutation.

### Added

- **Refund to store credit when cancelling an order.** `cancelOrder`'s new `refundMethod` input supports `storeCreditRefund` (with an optional `expiresAt` expiration date), so cancellations can now issue a refund as store credit — optionally time-limited — in addition to refunding the original payment method. Previously, the only option was a yes/no refund to the original payment method. (Builds on the `read_store_credit_accounts` / `write_store_credit_account_transactions` scopes added in 5.0.0.)
- **Clearer cancellation errors.** The `OrderCancelUserErrorCode` enum now covers all of Shopify's current codes, including store-credit-specific failures (`STORE_CREDIT_REFUND_B2B_NOT_SUPPORTED`, `STORE_CREDIT_REFUND_EXPIRATION_IN_PAST`, `STORE_CREDIT_REFUND_MISSING_CUSTOMER`, `NO_REFUND_TO_STORE_CREDIT_PERMISSION`) and `INTERNAL_ERROR`, so failed cancellations surface a meaningful reason instead of an unrecognized code.

### Changed

- **Cancel Order upgraded to Shopify Admin API `2026-04`** (from `2025-04`), keeping the action on the newest stable API version.

## [5.0.0] - Released

### Added

- **Expanded OAuth scopes** for richer Shopify Admin API coverage. The authorization flow now requests `read_merchant_managed_fulfillment_orders`, `write_merchant_managed_fulfillment_orders`, `read_third_party_fulfillment_orders`, `read_draft_orders`, `write_draft_orders`, `read_discounts`, `write_discounts`, `read_inventory`, `write_inventory`, `read_locations`, `read_metaobjects`, `write_metaobjects`, `read_metaobject_definitions`, `read_markets`, `read_order_edits`, `write_order_edits`, `read_returns`, `write_returns`, `read_checkouts`, `write_checkouts`, `read_price_rules`, `read_shipping`, `read_all_orders`, `write_customers`, `read_gift_cards`, `write_gift_cards`, `read_store_credit_accounts`, `read_store_credit_account_transactions`, `write_store_credit_account_transactions`, `read_content`, and `read_translations` in addition to the previously requested scopes. `read_all_orders`, `read_gift_cards`, and `write_gift_cards` are restricted scopes that Shopify must grant before they can be requested; Gladly has approval for these and they are now included in the OAuth request explicitly (Shopify approval alone does not include them — they still have to be listed in the `scope=` param). **Existing installations must re-authorize** to grant the new permissions — hence the major version bump.

## [4.8.1]

### Fixed

- **How line items are returned by order lookups** (`lookUpOrderByOrderNum`, `lookUpOrdersByEmail`, and the orders data pull):
  - **Custom items are now returned.** Line items with no catalog product (Shopify "custom items") were silently omitted from `lineItems`, so the visible items didn't add up to the order totals. They now come back like any other line item, with `product: null` and `variant: null` marking them as custom; catalog items are unchanged.
  - **Every line item now returns its price.** `LineItem.price` and `LineItem.totalDiscount` were always declared in the schema but never populated, so line items carried no pricing at runtime. They are now filled from Shopify's `originalUnitPriceSet.shopMoney` and `totalDiscountSet.shopMoney.amount` for catalog and custom items alike — `price × quantity` summed over `lineItems` matches the order subtotal.

## [4.8.0] - Release

### Added

- **`getMetafieldsByOwnerId` action**: fetch metafields for one or more Shopify resources in a single call. Accepts any GID whose type implements Shopify's `HasMetafields` interface (Product, ProductVariant, Order, DraftOrder, Customer, Collection, Article, Blog, Location, Page, Shop). Owner type is inferred from the GID, so callers can pass mixed ids in one request. Owners with no metafields are omitted from the result; unresolvable ids surface as `errors` with `code: "not_found"`. Scalar metafields only (no reference resolution); reference resolution is planned for a future v5 release.

### Removed

- **Metafields on the `lookUpOrderByOrderNum` action**: removed the optional `metafieldsCount` parameter and the `metafields` field from the action's order result. Order metafields are now fetched via the new `getMetafieldsByOwnerId` action (pass the order GID), which supersedes the per-action fetch. The customer and order data pulls continue to return metafields automatically.

## [4.7.0]

- **`sendOrderInvoice` action** (`shopify/actions/send_order_invoice/`): sends the customer an order invoice email containing a link to pay for the order. Wraps Shopify's `orderInvoiceSend` mutation; takes the order ID and triggers Shopify's invoice email. The order must be unpaid. An optional `email` input overrides the recipient address (mapped to Shopify's `EmailInput.to`); when omitted, Shopify sends to the order's default email address.

## [4.6.0]

- **`updateOrderMetadata` action** (`shopify/actions/update_order_metadata/`): new mutation that wraps Shopify's `orderUpdate` to write `tags` and `note` on an existing order. Uses Shopify's set-replace semantics — the agent supplies the complete desired tag list (typically by first fetching current tags via `lookUpOrderByOrderNum`). Passing `tags: []` clears all tags; omitting `tags` leaves them untouched. The same shape applies to `note`. Last-write-wins; no optimistic concurrency. Does not touch line items , shipping address, or any other order field.

## [4.5.0]

- **Product collections** (`Product.collections: [Collection!]`): the orders data pull and the `lookUpOrderByOrderNum` / `lookUpOrdersByEmail` actions now return up to 5 Shopify Collections (id + title) per product.

## [4.4.1] - Release

### Fixed

- **Orders Limit values configured via the admin UI are now correctly processed.** The admin UI form introduced in 4.4.0 persists all field values as strings, but the customer-orders pull, metafields pull, and `lookupOrdersByEmail` action all expected numeric input. As a result, any installation configured through the UI produced malformed GraphQL requests against Shopify — for example, the order-lookup action rendered `first: %!f(string=40)` instead of `first: 40`, causing requests to fail regardless of the value entered. Configuration values saved through the UI are now parsed correctly, restoring functionality for affected installations.
- **Invalid Orders Limit values now produce a clear error.** When the Orders Limit field is set to a non-numeric value (e.g. "forty") or a zero/negative number, the integration now fails fast with a message naming the bad value and the affected operation, instead of producing an opaque Shopify-side GraphQL error.

### Changed

- **`lookupOrdersByEmail` description clarified.** The action searches the email address provided at checkout, not the customer-account email. Guest orders placed under a different email from the customer's account will now be discoverable as expected; conversely, an account-holder's other orders won't appear unless they used the same email at checkout.
- **Orders Limit field hint** updated to communicate that a positive number is required.

## [4.4.0] – Released

### Added

- **App configuration UI form** (`shopify/ui/admin/form.json`): the Shopify app can now be configured from the Gladly admin UI, with inputs for the Shopify store subdomain (`configuration.shop`) and the recent-orders fetch limit (`configuration.ordersLimit`).

### Changed

- **GraphQL schema descriptions** in `data_schema.graphql` and `actions_schema.graphql` converted from `##` comments to proper triple-quote (`"""…"""`) descriptions, so that they appear in introspection and in Gladly action-parameter documentation. (#362)

### Fixed

- **Duplicate type names** in `actions_schema.graphql` removed; schema now passes `appcfg validate`.
- **`UpdateShippingAddress` `userErrors[].field`** is exposed as `[String!]`, matching the path-segment array form returned by Shopify (e.g. `["shippingAddress","city"]`).

## [4.2.0] and earlier

Released without a CHANGELOG. See `git log -- shopify/` for prior history.
