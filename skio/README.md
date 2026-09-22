# Skio

Subscription management for merchants on [Skio](https://skio.com). The app puts a customer's
Skio subscriptions on the Gladly profile and gives agents — and Gladly AI and Team Assist —
27 actions against Skio's GraphQL API, with the money-moving ones behind merchant-controlled
gates.

Built and maintained by Gladly. Authentication is a Skio API token in a request header.

## Read surface

One data pull, `storefront_user`, matches the Gladly customer to a Skio account by every email
address and mobile number on the profile, then loads their subscriptions, lines, discounts,
billing policy and delivery policy. A second pull, `subscriptions`, backs the card. Both are
declared in [`app/data/data_schema.graphql`](app/data/data_schema.graphql), which is the
source of truth for field-level meaning.

Matching is deliberately strict: exactly one Skio account matches, or the pull returns an
error. Subscriptions are returned in every status, so cancelled and paused history stays
visible; removed lines (`removedAt` set) are not.

The `skio-subscriptions` card shows each subscription's status, next billing date, billing and
delivery cadence, cycles completed, products, applied discounts and shipping address.

## Actions

Reads: `getSubscriptionById`, `getCustomers`, `getProductVariants`.

| Action | Gate |
|---|---|
| `skipSubscription` · `pauseSubscription` · `unpauseSubscription` | — |
| `updateNextBillingDate` · `subscriptionEditInterval` | — |
| `shipNow` | `enableShipNow` |
| `cancelSubscription` | `enablePermanentCancel` (permanent only) |
| `reactivateSubscription` | — |
| `setCommitmentOffer` | `enableCommitmentOffer` |
| `addSubscriptionLine` · `addSubscriptionLineBulk` | `enablePriceOverride` (custom price only) |
| `updateSubscriptionLine` · `updateSubscriptionLineBulk` | `enablePriceOverride` (custom price only) |
| `swapSubscriptionProductVariants` | `enablePriceOverride` (custom price only) |
| `setSubscriptionLineBackupVariant` | — |
| `updateDynamicBoxSubscription` | — (accepts variant and quantity only) |
| `updateSubscriptionShippingAddressV2` · `addAddressForStorefrontUser` | — |
| `changeSubscriptionDeliveryMethod` | `enableDeliveryPriceOverride` (when a price is set) |
| `setDeliveryPriceOverride` | `enableDeliveryPriceOverride` |
| `applyDiscountCode` | `allowedDiscountCodes` |
| `subscriptionUpdatePaymentMethod` | `enablePaymentMethodChange` |
| `updateSubscriptionNote` · `generateMagicLink` | — |

Two things Skio's API cannot do, so the app does not offer them: **refunds** (no mutation
exists; Skio's own docs send agents to Shopify) and **merging or splitting subscriptions**
(dashboard-only).

Five of the app's 24 write actions stamp provenance, so those changes are attributable to
Gladly — and to the calling agent — in Skio's own audit log. The field name is not the same
for all five, because Skio's own input types do not agree: `pauseSubscription`,
`skipSubscription`, `subscriptionEditInterval` and `updateNextBillingDate` send `debugCaller`,
while `shipNow` sends `caller`, which is the field `ShipNowInput` actually defines. Both spell
the same value — `Gladly`, or `Gladly:<caller>` when the caller identifies itself. The other
19 write actions send no provenance field.

## Merchant gates

Seven settings in the admin form decide which money-moving actions are available. Each one
**fails closed**: unset or unparseable means blocked, and the agent is told which setting an
admin has to change. `allowedDiscountCodes` is an allowlist — blank blocks every discount.

**The gate lives in the action template, never in the form.** Team Assist and Gladly AI call
actions directly and never render `app/ui/forms/`, so a hidden field or a disabled submit
button is an affordance for one of two callers, not access control. The signal a gate reads
has to be one the caller cannot satisfy on its own, which is why it is the merchant's admin
configuration: a model cannot invent a merchant's config value. Same pattern as
`apps/recharge`'s `adjust_credit`.

The `confirmed` checkbox on top of most gates is human-path friction. It is a `Boolean` that
defaults to off, so an omitted or false value is refused before Skio is called - but it slows a
mis-click down, it does not stop an API caller. Do not treat it as the control.

All seven gates run in `request_url.gtpl`, and each one has a blocked-state test dataset that
asserts the stop message rather than the URL - a gate that stopped blocking fails CI. The gate
matrix (App Factory, `docs/skio/build/live/`) then drives every gate live in both states
against a real store, which is the only place the whole path is proved end to end.

## Error translation

Four Skio errors send an agent to the wrong action, so each action's response
transformation rewrites exactly these and passes everything else through verbatim (a wrapper
that rewords every error hides the useful ones). All four confirmed live 2026-09-08.

The translated text arrives as **`errorMessage`** on the action's result, alongside `ok: false`
and the untranslated `errors`. It is set for both of Skio's failure shapes: a top-level
`errors` array, and a 200 whose mutation payload carries `ok: false` with a message. The
translation lives in the action, not the form, for the same reason the gates do - Gladly AI
and Team Assist never render a form, so anything that only a form does reaches one caller out
of two.

| Skio returns | What it actually means |
|---|---|
| `Lambda policy revoked, tried to access unauthorized user or subscription` | A well-formed subscription id that does not exist on this store. Reads as a permissions failure, so agents escalate a key rotation to IT over a wrong id. |
| `internal error` | A malformed id. Nothing in it points at the id. |
| `NOT_FOUND` | `applyDiscountCode` only: the code is on the merchant's allowlist but Skio does not have it. |
| `CURRENTLY_INACTIVE` | `applyDiscountCode` only: the code exists in Shopify but its schedule window is not open - the normal shape when a merchant allowlists a seasonal code early. |

`NOT_FOUND` / `CURRENTLY_INACTIVE` are the whole of `applyDiscountCode`'s vocabulary, and they
arrive as raw enums under `extensions.detail` - its `message` is always the useless "Apply
discount code failed". That is why the template prefers `extensions.detail` over `message`
everywhere.

## Configuration

Set in Org Manager, or with
[`appcfg apps config`](https://github.com/gladly/app-platform-appcfg-cli/blob/main/docs/appcfg_apps_config.md):

| Key | |
|---|---|
| `secrets.apiToken` | Skio API token — Skio admin → Settings → API |
| `configuration.allowedDiscountCodes` | Comma-separated codes agents may apply. Blank blocks all. |
| `configuration.enablePriceOverride` | Custom prices on lines, adds and swaps |
| `configuration.enableDeliveryPriceOverride` | Waiving or reducing shipping |
| `configuration.enableCommitmentOffer` | Turning a cancellation into required cycles |
| `configuration.enablePaymentMethodChange` | Changing the payment method on file |
| `configuration.enablePermanentCancel` | Irreversible cancellation |
| `configuration.enableShipNow` | Billing the card on file for an unscheduled order |

Every key a template reads must be present in the admin form, so a gate cannot drift into
being unreadable and silently permissive.

Agent forms are a separate, mandatory step: install and card placement do **not** surface
them. Eleven actions ship an agent form: `skipSubscription`, `pauseSubscription`,
`unpauseSubscription`, `subscriptionEditInterval`, `shipNow`, `cancelSubscription`,
`reactivateSubscription`, `updateSubscriptionShippingAddressV2`, `applyDiscountCode`,
`updateSubscriptionNote` and `generateMagicLink`. Register all 11 in Org Manager → External
App Forms, and read the existing rows before publishing — publish replaces the whole list.
The other actions have no form; Gladly AI and Team Assist call them directly.

## Development

`make all` — validate, test, build. This is the gate for a PR.

Card templates are not covered by `appcfg test`; it emits no template section at all, so a
card is only ever verified by looking at it on a real profile.

Live reads need `SKIO_API_TOKEN` (a `.env` here is read and gitignored):
`make storefront-user-data-graphql`, `subscriptions-data-graphql`, `storefront-user-data-pull`.

The write-path and merchant-gate drivers are not in this repo. They mutate a real store and
need a Skio dev store with a selling plan configured, so they cannot run in CI; they live in
the App Factory repo under `docs/skio/build/live/` (`gate-matrix` drives all seven gates in
both states, `state-matrix` the subscription transitions).

The app tree under `app/` is generated. Edit the generators in the App Factory repo
(`docs/skio/build/gen_*.py`), not the output. Hand-written exceptions:
`actions_schema.graphql`, `data_schema.graphql`, `app/ui/admin/form.json`, and the card
template. After regenerating, run `harvest_form_expectations.py` to re-capture form goldens and
`dedupe_fixtures.py` to re-link the shared fixtures. Re-harvest after any form edit — `appcfg test ui-form` prints nothing on a pass, so a dataset with no `expected_*` file
passes vacuously.

Each form's `_test_/data.json` is a symlink to the one shared fixture in `fixtures/`;
`_test_/` is excluded from the build zip.

## Reference

- [Skio API](https://code.skio.com/) · [data model](https://code.skio.com/#data-model)
- Skio depth limit is 4, and a root-level `Discounts` query times out — keep it nested.
