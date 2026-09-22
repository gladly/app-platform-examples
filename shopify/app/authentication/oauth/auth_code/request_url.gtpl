{{- /*
    required scopes:

    actions:
    `look_up_order_by_order_num`: read_orders, read_products
    `look_up_customer_by_email`: read_orders, read_products
    `cancel_order`: write_orders
    `refund_line_items`: write_orders
    `update_shipping_address`: write_orders

    data pulls:
    `shopify_customer`: read_customers
    `shopify_order`: read_customers,read_orders,read_products

    additional scopes (v5) — what they let us fetch / mutate:
    `read_merchant_managed_fulfillment_orders`, `write_merchant_managed_fulfillment_orders`:
        read and mutate FulfillmentOrder objects assigned to merchant-owned
        locations (their own warehouses/stores) — line-item-level
        fulfillment state, tracking, status transitions, accept/reject/
        cancel fulfillment requests.
    `read_third_party_fulfillment_orders`:
        read FulfillmentOrder objects assigned to locations managed by
        other fulfillment-service apps on the same shop (external 3PLs
        like ShipBob/ShipHero) so agents can see where an order is being
        fulfilled.
    `read_draft_orders`, `write_draft_orders`:
        read and mutate DraftOrder objects — pre-checkout carts that support
        agents build for Customers and convert into real Orders (used by the
        create/update/complete_draft_order actions).
    `read_discounts`, `write_discounts`:
        read and manage modern DiscountAutomatic / DiscountCode nodes via the
        Discounts Admin API (apply a percent/fixed/free-shipping discount to
        a draft order, list active promos).
    `read_price_rules`:
        read legacy PriceRule resources (REST) — older promo
        configurations that pre-date the Discounts Admin API. Needed
        only for merchants/apps still authoring promos as PriceRules
        instead of DiscountCode/DiscountAutomatic.
    `read_inventory`, `write_inventory`:
        read and adjust InventoryLevel quantities per Location for an
        InventoryItem (SKU) — stock counts and adjustments.
    `read_locations`:
        list a shop's Location records (physical/warehouse locations used to
        scope inventory and fulfillment orders).
    `read_metaobjects`, `write_metaobjects`:
        read and mutate Metaobject entries (instances) — merchant-defined
        custom data records (loyalty tiers, custom configuration, etc.).
    `read_metaobject_definitions`:
        read MetaobjectDefinition schemas (field types, validation
        rules, capabilities). Required to introspect what custom data
        types a merchant has set up before querying their entries.
    `read_order_edits`, `write_order_edits`:
        read and apply OrderEdit staged changes — add/remove line items,
        change quantities, adjust totals on an order placed after
        2019-01-01 (CalculatedOrder, OrderStagedChange). Lets agents
        modify post-purchase orders without canceling + re-creating.
    `read_returns`, `write_returns`:
        read and mutate Return objects — create/approve/decline RMAs,
        manage ReturnLineItem quantities and refundable state, drive
        reverse fulfillment orders and reverse deliveries. Lets agents
        process returns and exchanges on behalf of the merchant.
    `read_checkouts`, `write_checkouts`:
        read and mutate Checkout records — buyers who reached the
        checkout page but didn't complete the purchase. Includes
        email, line items, shipping address, abandoned-checkout
        recovery URL, and timing. Lets agents follow up on dropped
        carts, diagnose why a checkout failed, and (legacy) edit a
        checkout in flight.
    `read_markets`:
        read Market configuration — regions/countries enabled, per-market
        currency, language, domain/subfolder, price-list overrides. Lets
        Gladly reason about which market an order or customer belongs to.
    `read_shipping`:
        read CarrierService and shipping-zone configuration (carrier-rate
        providers, country-level shipping setup).
    `write_customers`:
        mutate Customer records — update profile fields, addresses,
        tags, marketing-consent state, etc. Paired with the existing
        `read_customers` scope.
    `read_gift_cards`, `write_gift_cards`:
        read and mutate GiftCard records — issue gift cards, look up
        balance / last-four / expiry, deactivate, adjust value via
        credit/debit transactions. Requires prior approval from
        Shopify (restricted scope; same posture as `read_all_orders`).
    `read_store_credit_accounts`:
        read StoreCreditAccount records — current balance, currency,
        owner (customer/company) for a customer's store credit
        wallet. Lets agents look up "how much credit does this
        customer have."
    `read_store_credit_account_transactions`, `write_store_credit_account_transactions`:
        read the credit/debit transaction history on a store credit
        account and issue new credits or debits (e.g. "refund $20 to
        store credit instead of original payment method", "comp $5
        for a service issue"). Reads cover StoreCreditAccountCredit/
        DebitTransaction; writes gate the storeCreditAccountCredit /
        storeCreditAccountDebit mutations.
    `read_content`:
        read Article, Blog, Comment, Page, and Redirect resources —
        store policies (refund/shipping/privacy), help/FAQ pages, blog
        posts. Lets agents surface merchant-authored content as
        reference when answering customers.
    `read_translations`:
        read TranslatableResource and Translation records — translated
        field values for each Locale/Market the merchant supports.
        Lets Gladly surface localized content for international
        customers.
    `read_all_orders`:
        lift the default 60-day window on the Order resource so order-lookup
        actions and data pulls can return historical orders. Used in
        conjunction with `read_orders` / `write_orders`. Requires prior
        approval from Shopify (granted to this app in the Partner Dashboard).

    additional scopes (v5.2):
    `read_companies`:
        read B2B Company, CompanyContact, and CompanyLocation records — the
        `purchasingEntity` on a draft order resolves to these for B2B orders.
        Required by `create_draft_order_from_order`; without it Shopify
        rejects the whole query with ACCESS_DENIED.
    `read_payment_terms`:
        read PaymentTerms records on orders and draft orders —
        payment-terms name/type (e.g. Net 30), days until due, and
        overdue state. Required for the `paymentTerms` field returned by
        `create_draft_order_from_order`; without it Shopify rejects the
        whole query with ACCESS_DENIED.

    more about access scopes: https://shopify.dev/docs/admin-api/access-scopes
*/ -}}
https://{{.integration.configuration.shop}}.myshopify.com/admin/oauth/authorize?client_id={{.integration.configuration.client_id}}&scope=read_customers,write_customers,read_companies,read_gift_cards,write_gift_cards,read_store_credit_accounts,read_store_credit_account_transactions,write_store_credit_account_transactions,read_orders,read_all_orders,read_products,write_orders,read_merchant_managed_fulfillment_orders,write_merchant_managed_fulfillment_orders,read_third_party_fulfillment_orders,read_draft_orders,write_draft_orders,read_payment_terms,read_discounts,write_discounts,read_price_rules,read_inventory,write_inventory,read_locations,read_metaobjects,write_metaobjects,read_metaobject_definitions,read_markets,read_order_edits,write_order_edits,read_returns,write_returns,read_checkouts,write_checkouts,read_shipping,read_content,read_translations&redirect_uri={{urlquery .oauth.redirect_uri}}&state={{.correlationId}}