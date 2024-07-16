{{/*
    Check if the response is empty (204 No Content) or if there's no raw data.
    - The purpose of this check is to handle the scenario where no order products are found for the given order_id.
    - If the response status code is 204 or there's no raw data, it means no order products exists.
*/}}
{{- if or (eq .response.statusCode 204) (eq .rawData nil) }}
    {{stop "No products found for BigCommerce order."}}
{{- else }}
    {{/*
        Iterate over all order products.
        - This loop processes each order product record individually.
    */}}
    {{- range .rawData -}}
        {{/*
            Remove key value pairs from the data because they are not needed.
            - This step cleans up unnecessary data before further processing.
        */}}
        {{- $_ := unset . "configurable_fields" }}
        {{- $_ := unset . "refund_amount" }}
        {{- $_ := unset . "order_pickup_method_id"}}
        {{- $_ := unset . "total_ex_tax"}}
        {{- $_ := unset . "cost_price_inc_tax"}}
        {{- $_ := unset . "cost_price_ex_tax"}}
        {{- $_ := unset . "base_price"}}
        {{- $_ := unset . "price_ex_tax"}}
        {{- $_ := unset . "price_tax"}}
        {{- $_ := unset . "cost_price_ex_tax"}}
        {{- $_ := unset . "wrapping_id"}}
        {{- $_ := unset . "weight"}}
        {{- $_ := unset . "width"}}
        {{- $_ := unset . "height"}}
        {{- $_ := unset . "depth"}}
        {{- $_ := unset . "fulfillment_source"}}
        {{- $_ := unset . "cost_price_tax"}}
        {{- $_ := unset . "base_wrapping_cost"}}
        {{- $_ := unset . "wrapping_cost_ex_tax"}}
        {{- $_ := unset . "wrapping_cost_tax"}}
        {{- $_ := unset . "wrapping_name"}}
        {{- $_ := unset . "wrapping_message"}}
        {{- $_ := unset . "fixed_shipping_cost"}}
        {{- $_ := unset . "ebay_item_id"}}
        {{- $_ := unset . "ebay_transaction_id"}}
        {{- $_ := unset . "option_set_id"}}
        {{- $_ := unset . "parent_order_product_id"}}
        {{- $_ := unset . "bin_picking_number"}}
        {{- $_ := unset . "product_options"}}
        {{- $_ := unset . "upc"}}
        {{- $_ := unset . "variant_id"}}
        {{- $_ := unset . "base_cost_price"}}
        {{- $_ := unset . "base_total"}}
        {{- $_ := unset . "discounted_total_inc_tax"}}
        {{- $_ := unset . "total_tax"}}
        {{- $_ := unset . "applied_discounts"}}
        {{- $_ := unset . "return_id"}}
        {{- $_ := unset . "event_name"}}
        {{- $_ := unset . "event_date"}}
        {{- $_ := unset . "is_bundled_product"}}
        {{- $_ := unset . "external_id"}}
        {{- $_ := unset . "name_customer"}}
        {{- $_ := unset . "name_merchant"}}
        {{- $_ := unset . "gift_certificate_id"}}
        {{- $_ := unset . "brand"}}
    {{- end}}
    {{- toJson .rawData -}}
{{- end}}
