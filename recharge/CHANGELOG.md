# Changelog

All notable changes to the Recharge app will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.2] - 2026-07-22

### Removed
- Removed the **store-credit adjustment ledger** (the `credit_adjustments` data
  pull and the "Recent adjustments" section of the Adjust store credit form).
  The store-credit **balance** and the **Adjust store credit** action are
  unchanged. The ledger pull fetched adjustments per credit account, which a
  newer `appcfg` validation rule rejects (a `dependsOnDataTypes` type must be
  referenced by a card); the balance pull is customer-scoped and unaffected. The
  ledger will return once it can fetch by customer and be verified against a
  store-credit-enabled (Plus/Custom) account. `read_credit_adjustments` is no
  longer required on the API token.

## [2.0.1] - 2026-07-11

### Changed
- Set an explicit `defaultValue: false` on the **Store credit** admin checkbox so
  it renders unchecked (store credit off) on a fresh install. Behavior is
  unchanged — an unset/unchecked value already disabled the credit pulls — this
  just makes the opt-in default explicit in the form definition.

## [2.0.0] - 2026-07-11

### Changed (BREAKING)
- **`LineItem.purchase_item_id` is now nullable (`ID!` → `ID`)** in both the
  actions and data schemas. A one-time free gift added via `add_free_gift` has no
  `purchase_item_id`, so the field must permit null; keeping it non-null caused any
  charge carrying a free gift to fail schema validation and blank the Recharge card.
  This is an incompatible output-type change from the deployed 1.1.0 baseline and so
  is a major version. No action/input renames or removals; all other schema changes
  since 1.1.0 are additive.

This 2.0.0 release also carries all the agent-action, card, and store-credit work
previously staged under the unreleased 1.2.0–1.5.3 entries below.

## [1.5.3] - 2026-07-11

### Changed
- Documented the **Delay next order** (`rescheduleNextCharge`) date format in the
  action's schema description: `resumeDate` is a date-only string in `YYYY-MM-DD`
  format and must be a valid future charge date. Previously this was only a source
  comment, invisible to Gladly AI and API callers.

## [1.5.2] - 2026-07-11

### Added
- **Store credit is now opt-in.** A new admin checkbox — "My Recharge plan
  includes store credit" (off by default) — gates the store-credit data pulls.
  Store credit is a Recharge Plus/Custom feature; on plans without it the credit
  endpoints return a permissions error, and because one failed data pull discards
  the whole refresh, that error previously blanked the entire Recharge card
  (subscriptions, charges, addresses included). With the box unchecked, Gladly
  never calls those endpoints, so stores without store credit get a fully working
  card with no configuration. Check the box (and grant the token the credit
  scopes) only if your plan includes store credit.

### Changed
- **Refund charge no longer requires any Payments scope.** Recharge's refund
  endpoint documents `Scopes: write_orders` — the same scope the other charge
  actions use — so the token needs no separate Payments permission (an earlier
  note incorrectly asked for one). Setup docs updated accordingly.
- **Full refunds now send an explicit amount** rather than Recharge's
  `full_refund` flag, so the dollars checked against the merchant's maximum are
  exactly the dollars refunded — closing a path where a caller could exceed the
  configured refund cap on a full refund.
- Clarified the admin **API Access Token** field hint (the value is the Recharge
  API token, labeled "API Key" in the Recharge admin).

## [1.5.1] - 2026-07-02

### Changed
- **Refund charge picker gates out Shopify Payments charges.** Recharge
  structurally cannot refund a charge processed by Shopify Payments (Shopify
  Checkout Integration — Shopify processes all refunds; confirmed live: the API
  rejects with "Refunds for Shopify Payments charges may only be initiated from
  Shopify"). Rather than let the agent submit and hit that rejection, the form
  now excludes those charges from the picker and explains why: a count line when
  other refundable charges exist ("N paid order(s) were processed by Shopify
  Payments and must be refunded in Shopify — they aren't listed here."), or a
  dedicated empty state when ALL paid orders are Shopify Payments. The gate
  fails open for any other/unknown processor (stripe, braintree, null) so a
  genuinely refundable charge is never wrongly hidden — Recharge's own rejection
  remains the backstop. This mirrors how Gorgias gates its Recharge refund
  button. Form-only change: no schema, config, or registration impact.

## [1.5.0] - 2026-07-02

### Added
- **Refund a charge** agent action in the compose (+) menu — refund a paid
  charge partially (an amount) or fully, backed by a new `refund_charge`
  mutation (`POST /charges/{id}/refund`). The charge picker offers only
  refundable charges (status `success`/`partially_refunded` with a remaining
  balance) and shows each order's refundable amount; a charge processed through
  Shopify's checkout must be refunded in Shopify and is rejected by Recharge.
  Guarded like Adjust store credit: the agent must type `approve`, and the
  dollars refunded (the amount, or a full charge's remaining balance) cannot
  exceed a merchant-configured maximum. For a full refund the form passes the
  charge's remaining balance so the cap can be enforced even though forms can't
  read configuration.
- Admin setting **Maximum refund amount** — the largest single refund an agent
  may issue from the Refund charge form.
- **Apply or remove discount** consolidated compose (+) form, backed by a new
  `manageChargeDiscount` mutation with an Operation dropdown (apply/remove) and
  a `ChargeDiscountOperation` enum — the same inverse-pair pattern as Cancel or
  reactivate / Skip or unskip. The standalone `apply_discount` and
  `remove_discount` actions **and their `applyDiscount`/`removeDiscount` forms
  are retained** (deleting a form folder is a breaking change that a same-major
  `appcfg apps upgrade` rejects). The consolidation happens at the org
  registration layer: only `manageChargeDiscount` is registered in External App
  Forms, so the (+) menu shows one entry while the deprecated forms stay in the
  bundle, unregistered, for backward compatibility.

## [1.4.0] - 2026-07-02

### Added
- Two more agent actions in the compose (+) menu: **Cancel or reactivate
  subscription** and **Skip or unskip next order**. Each is a single form with
  an Operation dropdown covering an inverse pair, backed by a new consolidated
  mutation (`manageSubscriptionState`, `manageNextChargeSkip`) that branches
  its Recharge endpoint on the chosen operation. The existing
  `cancel_subscription`, `reactivate_subscription`, `skip_next_subscription`,
  and `unskip_next_subscription` actions are unchanged and remain available to
  Gladly AI and the API — these are additive, not replacements. Two new enums,
  `SubscriptionStateOperation` and `NextChargeSkipOperation`. Verified additive
  via `appcfg compatible` against the prior build.

## [1.3.1] - 2026-07-01

### Fixed
- **Adjust subscription price**: `action_result.gtpl`'s branch selection now
  uses an exact match against the confirmation guard's one static literal
  message instead of a `contains`/`lower` substring check, removing a
  needless dependency on cross-engine string-matching semantics. This is a
  hardening change, not a fix for the anchoring symptom below — see Known
  issues.

### Known issues (platform, not app code)
- **Adjust subscription price**: live-tested 2026-07-01 against the UAT
  sandbox — any rejection with `"attr": "price"` (over-cap, non-decimal,
  zero/negative, cap-not-configured) renders on the **Subscription** row
  instead of the **New price** row; `"attr": "confirmationCopy"` renders
  correctly. Reproduced repeatedly across fresh form instances and a full
  page reload, so this isn't stale client state. `price` is also a field
  name on the `Subscription` data type (used for the card and the select
  labels); `subscriptionId` and `confirmationCopy` aren't used elsewhere in
  the schema and route correctly. Suspected cause: the agent desktop's
  inline-error-to-field resolution collides across the action's input names
  and the data type's field names rather than scoping to the current
  mutation. Renaming the `price` input to something unique (e.g.
  `newPrice`) would very likely fix this, but is a breaking, non-additive
  schema change per `appcfg compatible` (confirmed rejecting the input
  *removal* in the same field earlier this session) — not done here. Rec
  #12 (anchor the over-cap error to the price field) is only partially
  achieved: the JSON `attr` is correct, but the platform doesn't render it
  there. Worth reporting to Gladly platform engineering; the message text
  itself still names the field and value, so agents aren't blocked, just
  not visually pointed at the right row.

## [1.3.0] - 2026-07-01

### Added
- Agent actions surfaced in the compose (+) menu: **Delay next order**
  (reschedule the next charge), **Change subscription frequency**, **Update
  shipping address**, **Add free gift**, and **Adjust store credit**.
- **Adjust store credit** is guarded: the agent must type `approve` to confirm,
  and the amount cannot exceed a merchant-configured maximum.
- Subscriptions profile card showing each customer's subscriptions and payment
  status (active/total counts, dunning and valid-payment-method flags, and
  per-subscription product, status, price, billing frequency, and next charge).
- Store-credit data pulls: credit accounts and credit adjustments (ledger),
  with `CreditAccount` and `CreditAdjustment` data types.
- Top-level `subscriptions`, `addresses`, `charges`, `credit_accounts`, and
  `credit_adjustments` query fields so agent UI forms can bind their form data.
- Admin setting **Maximum credit adjustment** — the largest single store-credit
  adjustment an agent may submit.
- Three more agent actions in the compose (+) menu: **Apply discount** (apply a
  discount/coupon code to a charge — use the queued charge for a next-order
  discount; a companion **Remove discount** action reverses it), **Swap
  subscription item** (change a subscription to a different Shopify variant by
  variant id), and **Adjust subscription price** (set a subscription's line-item
  price).
- **Adjust subscription price** is guarded like Adjust store credit: the agent
  must type `approve` to confirm, and the price cannot exceed a
  merchant-configured maximum.
- Admin setting **Maximum subscription price** — the largest line-item price an
  agent may set from the Adjust subscription price form.
- **Delay next order** options are now cadence-relative — computed from each
  subscription's own billing interval (weekly/monthly/every-2-months all fit) —
  bounded by a new admin setting **Maximum delay (billing cycles)** (default 3).
  Prepaid subscriptions are shown a "can't delay here" note.
- **Remove discount** agent form — the `remove_discount` action already
  shipped; this closes the gap so agents can reverse a mistakenly-applied
  discount without escalating to Recharge admin.

### Changed
- **Apply/Remove discount** charge picker now leads with the customer's next
  (queued) order and uses plain-language labels ("Next order — ships
  <date>", "Retrying order — was <date>") instead of raw charge ids and
  Recharge status words; options already carrying a discount are flagged
  ", has discount". When exactly one queued charge exists, the form
  confirms which order the discount applies to. The same status labels now
  apply to **Add free gift**'s charge picker.
- **Apply discount** failures are now actionable ("Recharge couldn't apply
  this code: … Check the spelling and that the promotion is active …") and
  success detail echoes the order's ship date instead of a bare charge id.
- **Swap subscription item**: the subscription picker now filters to
  swappable subscriptions only (`is_swappable`, null-safe) with a diagnostic
  empty state, instead of listing every active subscription and failing
  only after submit. The optional **Price override** field was removed from
  the agent form — it previously bypassed the price cap enforced by Adjust
  subscription price. The underlying action field is unchanged (removing it
  from the schema is a breaking change for existing configurations), but it
  is now guarded with the exact same merchant-configured maximum as Adjust
  subscription price, rejected before calling Recharge; agents should swap
  the item, then use the guarded Adjust price action if the price also needs
  to change. The variant-id field is now labeled "Shopify variant ID" with a
  sourcing hint, a subscription-level rejection (e.g. not swappable) is now
  framed in plain language instead of blaming the variant id field, and a
  successful swap echoes the resulting product/variant/price.
- **Adjust subscription price**: an over-the-cap rejection now anchors to
  the New price field (previously the confirmation field), the subscription
  picker shows the current price with its currency code, the New price hint
  no longer references an invisible cap number, and the confirmation hint
  asks the agent to re-read the subscription and price before typing
  `approve`.
- **Adjust store credit** submit button relabeled "Adjust credit" for
  consistency with the other guarded actions' verb+object submit labels.
- Subscription select/option labels across Swap subscription item and
  Adjust subscription price now use a canonical
  `{product title} — #{id} — {context}` prefix so two subscriptions with
  the same product title are never ambiguous before a money-moving change.
- Subscriptions profile card: billing frequency is now visible in the
  collapsed subscription summary, discount eligibility moved into the expanded
  details, short Price and Quantity values no longer force right alignment in
  narrow cards, customer-summary labels now use compact copy (`Active subs`,
  `Total subs`, `Customer ID`) to avoid truncation, and each subscription's
  `Updated` date now appears alongside Created/Cancellation details.
  Subscriptions still sort active-first in the
  data pull so cancelled subscriptions don't dilute the ones agents act on;
  the card still includes a distinct per-subscription banner for exhausted
  payment retries (new `maxRetriesReached` field, separate from the existing
  "payment method in dunning" signal, which is still retrying); the dunning
  banner copy was de-jargoned; and the customer ID renders below the
  payment-health banners instead of above them.

## [1.1.0] - 2026-04-21

### Added
- Admin UI configuration form (`ui/admin/form.json`).

### Changed
- Updated hint text (2026-04-22).

## [1.0.0] - 2025-01-29

### Added
- Initial Recharge app: subscription management actions (`cancel_subscription`, `get_subscription_by_id`, `reactivate_subscription`, `skip_next_subscription`, `unskip_next_subscription`); customer addresses and charges data pulls; authentication.

### Changed
- Iterative refinements to actions and data-pull schemas (2025-01-31 to 2025-02-17).

### Fixed
- appcfg validation issues (2025-02-05).
