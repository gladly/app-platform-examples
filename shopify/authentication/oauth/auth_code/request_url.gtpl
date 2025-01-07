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

    more about access scopes: https://shopify.dev/docs/admin-api/access-scopes
*/ -}}
https://{{.integration.configuration.shop}}.myshopify.com/admin/oauth/authorize?client_id={{.integration.configuration.client_id}}&scope=read_customers,read_orders,read_products,write_orders&redirect_uri={{urlquery .oauth.redirect_uri}}&state={{.correlationId}}