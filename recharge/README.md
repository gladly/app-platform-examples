# Recharge App for Gladly Sidekick

The Recharge app for Gladly Sidekick surfaces each customer's Recharge
subscriptions and payment status on the Gladly customer profile, and lets agents
manage those subscriptions — reschedule the next charge, change billing
frequency, update the shipping address, add a free gift, apply a discount, swap
a subscription's item, adjust a subscription's price, adjust store credit,
cancel or reactivate a subscription, skip or unskip its next order, and refund
a charge — without leaving the conversation.

## Benefits

- Give agents a real-time view of every customer's Recharge subscriptions and
  payment health directly on the customer profile.
- Resolve common subscription requests inside the conversation, without
  switching to the Recharge admin.
- Reduce involuntary churn by spotting failed payments (dunning) and
  rescheduling, re-billing, or adjusting subscriptions on the spot.

## Example Use Cases

- A customer asks to delay their next order — the agent reschedules the next
  charge to a later date.
- A customer wants their subscription less often — the agent changes the billing
  frequency.
- A customer moved — the agent updates the shipping address on the subscription.
- A goodwill gesture — the agent adds a one-time free gift to an upcoming
  (queued) charge.
- A promo or apology — the agent applies a discount code to the customer's next
  (queued) charge, or removes one applied by mistake.
- A customer wants a different flavor or size — the agent swaps the subscription
  to a different Shopify variant.
- A pricing correction — the agent sets a subscription's line-item price,
  bounded by a merchant-configured maximum.
- A billing correction — the agent adds or removes store credit, bounded by a
  merchant-configured maximum.
- A customer wants to cancel, or changes their mind after cancelling — the
  agent cancels or reactivates the subscription from the same form.
- A customer wants to push out their next order by one cycle, or restore an
  order they (or another agent) skipped by mistake — the agent skips or
  unskips the next charge from the same form.
- A customer was overcharged or is owed money back — the agent refunds the
  charge, partially or in full, bounded by a merchant-configured maximum (for
  orders Recharge processed itself).

## Available Actions

The Recharge app supports the following management actions from the agent's
compose (+) menu:

- **Delay next order**
  Move a subscription's next charge to a later date. This only defers the next
  charge — it does not pause the subscription.

- **Change subscription frequency**
  Change how often a subscription renews (order interval frequency and unit).

- **Update shipping address**
  Update a customer's shipping address. Only the fields you supply are changed;
  changing the country also requires a ZIP valid for the new country.

- **Add free gift**
  Add a one-time free gift line item to a queued or errored charge, identified
  by its Shopify external variant id.

- **Apply discount** / **Remove discount**
  Apply a discount/coupon code to a charge (use the queued charge to discount the
  customer's next order), or remove a previously applied discount.

- **Swap subscription item**
  Swap a subscription's item to a different Shopify variant, identified by its
  external variant id. Recharge fills in the product, price, and SKU from the
  variant. Only swappable subscriptions are offered; one-time or otherwise
  non-swappable items must be changed directly in Recharge. This action never
  sets a price — use Adjust subscription price for that.

- **Adjust subscription price**
  Set a subscription's line-item price. This action is guarded: the agent must
  type `approve` to confirm, and the price cannot exceed the merchant-configured
  maximum.

- **Adjust store credit**
  Add (credit) or remove (debit) store credit on a customer's credit account.
  This action is guarded: the agent must type `approve` to confirm, and the
  amount cannot exceed the merchant-configured maximum.
  **Requires a Recharge plan that includes store credit** (the Plus and Custom
  tiers; not the Starter plan — see <https://getrecharge.com/pricing/>). On a
  plan without store credit there are no credit accounts to act on, so skip this
  action and its credit scopes; the rest of the app is unaffected.

- **Cancel or reactivate subscription**
  Cancel an active subscription or reactivate a cancelled one, chosen from an
  Operation dropdown in a single form. The subscription picker lists both
  active and cancelled subscriptions, labeled with their status, so the right
  target is always one selection away regardless of which operation you pick.
  Reason and Send email apply only when cancelling; Recharge ignores them when
  reactivating.

- **Skip or unskip next order**
  Skip a subscription's upcoming charge, or restore one that was previously
  skipped, chosen from an Operation dropdown in a single form. The order
  picker lists queued, retrying, and already-skipped orders, labeled with
  their status, so either direction is reachable from the same list.

- **Refund a charge**
  Refund a paid charge, partially (an amount) or in full. This action is
  guarded: the agent must type `approve` to confirm, and the amount cannot
  exceed the merchant-configured maximum. Only charges Recharge processed itself
  can be refunded — charges processed by Shopify Payments are excluded from the
  picker and must be refunded in Shopify.

The app also ships the underlying subscription-lifecycle actions
`cancel_subscription`, `reactivate_subscription`, `skip_next_subscription`, and
`unskip_next_subscription`, plus a `get_subscription_by_id` lookup, unchanged
and available to Gladly AI and via the API — the two forms above are the
human-agent-facing equivalent, added as `manageSubscriptionState` and
`manageNextChargeSkip` so agents don't need four separate compose-menu items
for what are really two toggles.

## Data Pulls

### What Data Is Available?

With Recharge, you get a complete picture of each customer, including:

- Active and total subscription counts, and payment health (whether a payment
  method is in dunning and whether a valid payment method is on file).
- Each subscription: product, status, price, quantity, billing frequency, next
  charge date, SKU, and variant.
- Customer shipping addresses.
- Charges — upcoming and historical, including queued and errored charges.
- Store-credit account balance and currency (only on Recharge plans that include
  store credit — the Plus and Custom tiers — and only when enabled in the app
  configuration).

This gives your agents a real-time, single view of every customer's Recharge
subscription journey — so you can answer questions, resolve issues, and reduce
churn quickly.

Note: The subscriptions card shows both active and cancelled subscriptions;
cancelled subscriptions display their cancellation date and reason.

### How Does Customer Matching Work?

Recharge finds the right customer by matching data from their customer profile:

- Primary email address.
- Any email address on the profile (first match).

If exactly one Recharge customer matches, you'll see their subscriptions and
payment status. If no match — or more than one match — is found, no customer's
data is returned.

# Recharge App Toolkit

## Who maintains the integration

The Recharge integration is built and maintained by Gladly.

## How the integration works

The Recharge app integrates with the Recharge platform via its REST API
(<https://developer.rechargepayments.com/2021-11>). This lets Gladly query
subscription, address, charge, and store-credit data and perform mutations such
as rescheduling a charge, changing frequency, updating an address, adding a free
gift, applying or removing a discount, swapping a subscription's item, adjusting
a subscription's price, adjusting store credit, and refunding a charge.

Authentication is handled with a Recharge API token sent in the
`X-Recharge-Access-Token` request header (API version `2021-11`).

## Setup & Installation

### Before You Start

You need a Recharge API token with read and write access to customers,
subscriptions, orders/charges, and addresses. The **Refund charge** action runs
on the same **Orders — Read and Write** (`write_orders`) scope the other charge
actions use — Recharge's refund endpoint documents `Scopes: write_orders` and no
payments scope, so you do **not** need to grant the token any Payments
permission for refunds.

Store credit is **off by default** and opt-in. It's only available on Recharge
plans that include it — the **Plus and Custom** tiers, not the Starter plan (see
<https://getrecharge.com/pricing/>). To use the **Adjust store credit** action
and show the credit balance, check **"My Recharge plan includes store credit"**
in the app's admin config (below) *and* grant the token the credit scopes
(`read_credit_accounts`, `write_credit_adjustments_credit`,
`write_credit_adjustments_debit`). Leave it unchecked otherwise — and only check
it once the scopes are in place: if Gladly calls the store-credit endpoints
without the scopes it gets a permissions error, and because a single failed data
pull discards the whole refresh, that error would blank the entire Recharge card,
not just the credit data.

Note that Recharge can only refund charges it processed itself. On the Shopify
Checkout Integration, charges are processed by **Shopify Payments** and must be
refunded in Shopify — the Refund charge form therefore excludes those charges
from its picker and tells the agent to refund in Shopify instead. Charges on a
Recharge-managed gateway (Recharge Payments, Stripe, Braintree, Authorize.net,
BigCommerce/headless stores) are refundable here.
Set each resource's permission to Read and Write on the token's edit page in
the Recharge admin.

### Installation

Create the API token in the Recharge admin under **Tools & Apps → API tokens**
with the scopes above, then provide it when installing the app in Gladly. The
admin setup form collects:

- **Access Token** (required) — your Recharge API token.
- **Store credit** (checkbox, off by default) — check "My Recharge plan includes
  store credit" only if your plan includes store credit and the token has the
  credit scopes. Off by default so stores without store credit never call those
  endpoints (which would otherwise error and blank the card).
- **Maximum credit adjustment** (required only if agents will use the Adjust
  store credit action) — the largest single store-credit adjustment an agent
  may submit; larger amounts are rejected and must be handled by a manager in
  Recharge.
- **Maximum subscription price** (required only if agents will use the Adjust
  subscription price action) — the highest line-item price an agent may set;
  larger amounts are rejected and must be handled by a manager in Recharge.
- **Maximum refund amount** (required only if agents will use the Refund charge
  action) — the largest single refund an agent may issue (the partial amount,
  or a full charge's remaining balance); larger refunds are rejected and must be
  handled by a manager in Recharge.

## Recharge Custom App

The source for this app lives in this repository under `apps/recharge/app/`.
