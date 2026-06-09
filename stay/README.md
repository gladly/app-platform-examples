# Stay.ai App for the Gladly App Platform

Surfaces every Stay.ai subscription on the Gladly customer profile — status, next billing date, billing cadence, prices, line items with images, delivery address, and churn context — and gives agents five actions: pause (with a required resume date), unpause, skip next order, cancel (with a structured reason), and generate a customer portal link.

Stay.ai is a Shopify subscription platform (formerly Retextion); its API lives at `api.retextion.com/api/v2` and authenticates via the `X-RETEXTION-ACCESS-TOKEN` header.

## Layout

```text
Makefile          appcfg validate / test / build targets
app/
  manifest.json
  ui/admin/form.json              admin setup form (API key + cancellation reasons)
  authentication/headers/         X-RETEXTION-ACCESS-TOKEN header template
  data/
    data_schema.graphql           collection-rooted schema (no customer type)
    pull/subscriptions/           list pull: GET /subscriptions/?email=...
    pull/subscription_details/    detail fan-out: GET /subscriptions/{id} per subscription
  actions/                        five POST actions + shared result envelope
```

## Development

```bash
make validate   # appcfg validate -r app
make test       # appcfg test -r app
make build      # appcfg build -r app
```

Live-run fixtures live in `_run_/data/` per pull and action:

```bash
appcfg run data-pull -s '{"apiKey":"<real key>"}' -d default -r app
appcfg run action cancel_subscription -s '{"apiKey":"<real key>"}' -d default -r app
```

## Architecture notes

- **Two chained pulls.** Stay's list endpoint lacks line-item titles/images, billing policy, delivery address, and line count — those exist only on the per-subscription detail endpoint. The `subscriptions` pull lists by customer email; `subscription_details` depends on it and emits one GET per subscription (multi-URL fan-out). Detail fields are nullable in the schema, so a failed or lagging detail pull degrades to a list-data card rather than a blank one. This is the repo's first detail-chained subscriptions example.
- **Collection-rooted schema.** Stay has no customer-lookup endpoint, so the schema root is `Query { subscriptions }` with no customer type (the loop_order_tracking / shiphero shape); the detail type parents to the subscription via `@parentId`.
- **Status flattening.** The detail endpoint nests status as `{status, pausedUntil}`; the transformation flattens it so displayed status is consistent across both pulls.
- **Action result envelope.** Every action returns `{error, subscription}` (or `{error, url}` for the portal link). The whole 4xx range maps to a legible `error` logged to the conversation timeline — the safety net for stale cards when the subscription's state changed since the last pull. Only unexpected statuses `fail`.
- **Cancellation reasons.** The admin form takes an optional comma-separated reason list. When configured, the cancel action rejects reasons outside the list (`stop` before any API call). Initiator is always `MERCHANT` — agents act on the merchant's behalf — keeping Stay churn analytics truthful.
- **Sidekick-legible schema.** Field descriptions are written in customer-service language (e.g. `billingCadence: "Every 2 months"` is safe to read to a customer verbatim) so Sidekick can consume the same data the card renders.

## Known limitations

- **100-subscription cap.** App Platform data pulls cannot paginate; the list pull requests Stay's maximum `pageSize=100`, sorted `updatedAt` descending so truncation drops the stalest records first.
- **Action gating is card-side.** Action templates receive only `integration` + `inputs`, so per-status button visibility (pause/skip on active, unpause on paused, cancel on active and paused) is configured on the Gladly card; the error envelope is the backstop. There is no reactivate action; an accidental cancel is recovered via the portal link or the Stay admin.
- **Fixtures are OpenAPI-derived.** Test data was authored from Stay's published API reference ([docs.stay.ai](https://docs.stay.ai)). Before production use, re-record fixtures from a Stay sandbox (sanitizing PII to synthetic stand-ins) and reconcile shapes — especially date wire formats and the `pausedUntil` input format on the pause endpoint.
- **Misconfigured API key.** A 401 from Stay means pulls never populate; agents see no subscription section. Verify the key in Stay's Merchant Portal (Settings > API Keys) if the card stays empty for known subscribers.
