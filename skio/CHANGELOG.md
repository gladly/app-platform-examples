# Changelog

All notable changes to the Skio app will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [5.0.0] - 2026-09-09

### Changed (BREAKING)
- **`shipNow` now requires the merchant to enable it.** An admin has to set "Allow AI and
  agents to ship the next order now" in the Skio app configuration; until they do, ship-now
  is blocked and the caller is told which setting to change. Existing installs lose ship-now
  on upgrade until an admin enables it.
- Why: shipping the next order now charges the customer's card ahead of schedule, and Gladly
  AI can call actions directly without a person reviewing a form first. That is the one
  pre-existing action in this release that moves money, so it is now the merchant's decision
  rather than on by default.
- **Confirming a sensitive action is now a checkbox rather than a typed word.** The eleven
  actions that ask for confirmation take a `confirmed` true/false input in place of the
  `confirmationCopy` text that had to read exactly "approve". The guard is unchanged in
  strength: confirmation still defaults to off and an omitted value is refused. This removes
  a typo as a reason for a refused action, and matches how other Gladly apps confirm.

### Added
- **11 agent forms**, from none: skip the next order, pause, un-pause, change the billing
  interval, ship the next order now, cancel, reactivate, change the shipping address, apply a
  discount code, edit the subscription note and send the customer an account portal link -
  from the Gladly compose menu, without leaving the conversation. The other actions have no
  form of their own; Gladly AI and Team Assist call them directly.
- Agent forms need a one-time setup step: an admin registers them in Org Manager -> External
  App Forms. Install and card placement do not surface them, and publishing replaces the whole
  list, so read the existing rows first.
- **22 net-new actions (27 total)**, every Skio mutation a CX team could reasonably drive:
  `subscriptionUpdatePaymentMethod`, `setCommitmentOffer`, `changeSubscriptionDeliveryMethod`,
  `swapSubscriptionProductVariants`, `setSubscriptionLineBackupVariant`,
  `updateDynamicBoxSubscription`, `generateMagicLink` and the rest. All 27 are callable by
  Gladly AI and Team Assist; eleven of them also have an agent form.
- **Seven merchant gates** in the admin form: a discount-code allowlist and toggles for price
  override, shipping-price override, commitment offers, payment-method change, permanent
  cancellation and ship-now. Each fails closed - unset or unparseable means blocked, and the
  caller is told which setting an admin must change.
- **`errorMessage` on every action result.** Four Skio errors send an agent to the wrong
  action - a non-existent subscription id comes back as "Lambda policy revoked", which reads
  as a permissions failure; a malformed id comes back as "internal error"; a discount code
  Skio no longer holds comes back as `NOT_FOUND`; one outside its schedule window as
  `CURRENTLY_INACTIVE`. All four are now translated into what they actually mean, for every
  caller including Gladly AI and Team Assist. Every other Skio message passes through
  verbatim.
- Five of the 24 write actions stamp provenance, so those changes are attributable to Gladly -
  and to the calling agent - in Skio's audit log: `pauseSubscription`, `skipSubscription`,
  `subscriptionEditInterval` and `updateNextBillingDate` send `debugCaller`, and `shipNow`
  sends `caller`, which is the field `ShipNowInput` defines. The other 19 write actions send
  no provenance field.

### Fixed
- `getCustomers` matched `email` case sensitively, so `Jane@Example.com` never found
  `jane@example.com`, and it promised fields its query never requested (they were always
  null).
- A quote character in a `getCustomers` email or phone number could alter the lookup filter
  and return customers that were not asked for. Lookup values are now escaped.
- Five action payloads read only `__typename`, which cannot tell success from silent failure.
- Change shipping address and Set backup variant reported success whenever Skio answered at
  all, so a rejected address change ("Address could not be validated") was shown to the agent
  as a completed one. Both now report Skio's own result.
- Change the payment method, change the billing interval, set a commitment offer and override
  the shipping price completed the change in Skio but left the success flag off their result,
  which every failure sets to false. An automated caller that checks that flag read a completed
  change as a failure. All four now report success.
- Clearing a subscription note did not work. An agent who emptied the note field was told the
  note had been cleared while the old note survived, because a blank value was dropped instead
  of being sent to Skio. Emptying the field now clears the note.
- A pause window, a skip window and a billing or prepaid interval count are now rejected
  unless they are a whole number of one or more. A zero was accepted and did nothing, while
  Skio still reported success, so an agent was told a subscription had been paused when it
  had not.
- Changing a billing frequency or a prepaid cadence now requires both the interval and its
  count. Supplying one without the other changed the unit and silently left the count alone,
  so "every two months" quietly became monthly and the agent was told it had worked.

## [4.3.0] - 2026-09-09

### Added
- The customer profile now carries every address on the Skio account, along with
  each subscription's discounts, shipping address,
  billing/delivery/prepaid-delivery policies, note and custom attributes.
- Prepaid subscriptions show their renewal date and the deliveries left in the
  prepaid term.
- Skio's churn score and the reasons behind it are available on the profile, so
  a Rule can route a high-risk conversation to retention.

### Fixed
- Email lookup is now case-insensitive: a profile holding `Jane@Example.com`
  finds the Skio customer stored as `jane@example.com`. Previously it matched
  case-sensitively and quietly returned an empty card.
- Subscription and line lists are now bounded, so a customer with many
  subscriptions or products no longer runs into Skio's response cap and comes
  back empty.
- Card rows that read `NaN` now render their values, and a permanently
  cancelled subscription reads "Permanently Cancelled" in full instead of being
  truncated to "Cancelled...", which hid the fact that it cannot be reactivated.
- The shipping price row reads "Overridden in Skio" rather than attributing the
  override to an agent.
- Discount codes applied to a subscription line now appear on the profile; Skio
  attaches them to the line rather than the subscription, so they were
  previously invisible.
- Discount percentage labels are rounded for display, so a code always reads
  like "7% off" and never as a long run of decimal places.
- A fixed-amount discount now shows its amount as money — "$186.29", matching
  the price row beside it — instead of the bare number "186.29 off". The redeem
  code still shows on its own row.
- Churn risk reads as a percentage ("22%") instead of the raw score, which
  rendered as a long decimal cut off mid-digit by the column. The underlying
  score is unchanged for Rules and Gladly AI.
- A subscription line whose Shopify product was deleted no longer blanks the
  whole card.
- An error from Skio is now reported as an error instead of as a customer with
  no Skio account.
- A profile with no email address or mobile number no longer sends Skio a
  filter that matches nothing.
- A discount with no redeem code now shows its name, so several fixed-amount
  discounts on one line no longer render as identical money rows with nothing
  to tell them apart.
- Card row labels no longer truncate in the narrow label column, so an agent
  can read what each row is without hovering.

## [4.2.0] - 2026-08-21

### Fixed
- Prepaid subscription lines with 0 orders remaining now keep that value in
  pulled data (previously dropped, so the card couldn't show "0 remaining").

### Added
- Customer profile card (`skio-subscriptions`) showing each Skio
  subscription's status, next billing date, billing/delivery cadence,
  cycles completed, and products, with the Skio customer and
  subscription IDs for quick lookups.

## [4.1.0] - 2026-04-22

### Added
- Admin UI configuration form (`ui/admin/form.json`).

### Changed
- Updated hint text (2026-04-27).

## [4.0.0] - 2025-12-19

### Changed
- `cancelSubscription` now takes primitive inputs (incompatible change).

## [3.1.0] - 2025-05-12

### Changed
- Cleanup.

## [3.0.0] - 2025-05-12

### Changed
- `pauseSubscription` flattened (incompatible signature change).
- Manifest updated to reflect incompatible changes.

## [2.0.0] - 2025-05-01

### Changed
- Upgrade to v2.

## [1.1.0] - 2025-05-01

### Changed
- Moved Makefile out of the `app/` directory.

## [1.0.0] - 2025-04-15

### Added
- Initial Skio app: subscription management actions (`cancelSubscription`, `getCustomers`, `getSubscriptionById`, `pauseSubscription`, `shipNow`); customer/subscription data pulls; authentication.
- Iterative test coverage and cleanup during initial buildout (through 2025-04-30).
