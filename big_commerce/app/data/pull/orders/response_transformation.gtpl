{{/*
    Check if the response is empty (204 No Content) or if there's no raw data.
    - The purpose of this check is to handle the scenario where no orders are found for the given BigCommerce customer id.
    - An empty array is returned to indicate that no orders were found.
*/}}
{{- if or (eq .response.statusCode 204) (eq .rawData nil) }}
[]
{{- else }}
    {{/*
        Iterate over all orders.
        - This loop processes each order record individually.
    */}}
    {{- range .rawData -}}
        {{/* date_created value needs to be converted into ISO8601 format in order to be processed by Gladly Sidekick. */}}
        {{- if and (ne .date_created "") (ne .date_created nil) }}
            {{- $_ := set . "date_created" (dateInZone "2006-01-02T15:04:05Z" (toDate "Mon, 02 Jan 2006 15:04:05 -0700" .date_created) "UTC") }}
        {{- else }}
            {{- $_ := set . "date_created" nil }}
        {{- end}}

        {{/* date_shipped value needs to be converted into ISO8601 format in order to be processed by Gladly Sidekick. */}}
        {{- if and (ne .date_shipped "") (ne .date_shipped nil) }}
            {{- $_ := set . "date_shipped" (dateInZone "2006-01-02T15:04:05Z" (toDate "Mon, 02 Jan 2006 15:04:05 -0700" .date_shipped) "UTC") }}
        {{- else }}
            {{- $_ := set . "date_shipped" nil }}
        {{- end}}
        
        {{/* date_modified value needs to be converted into ISO8601 format in order to be processed by Gladly Sidekick. */}}
        {{- if and (ne .date_modified "") (ne .date_modified nil) }}
            {{- $_ := set . "date_modified" (dateInZone "2006-01-02T15:04:05Z" (toDate "Mon, 02 Jan 2006 15:04:05 -0700" .date_modified) "UTC") }}
        {{- else }}
            {{- $_ := set . "date_modified" nil }}
        {{- end}}

        {{/*
            Remove key value pairs from the data because they are not needed.
            - This step cleans up unnecessary data before further processing.
        */}}
        {{- $_ := unset .billing_address "form_fields" }}
        {{- $_ := unset . "credit_card_type" }}
        {{- $_ := unset . "ip_address" }}
        {{- $_ := unset . "ip_address_v6" }}
        {{- $_ := unset . "payment_method" }}
        {{- $_ := unset . "payment_provider_id" }}
        {{- $_ := unset . "consignments" }}
        {{- $_ := unset . "shipping_cost_tax" }}
        {{- $_ := unset . "shipping_cost_tax_class_id" }}
        {{- $_ := unset . "handling_cost_tax" }}
        {{- $_ := unset . "handling_cost_tax_class_id" }}
        {{- $_ := unset . "wrapping_cost_tax" }}
        {{- $_ := unset . "wrapping_cost_tax_class_id" }}
        {{- $_ := unset . "currency_id" }}
        {{- $_ := unset . "currency_exchange_rate" }}
        {{- $_ := unset . "custom_status"}}
        {{- $_ := unset . "default_currency_id" }}
        {{- $_ := unset . "default_currency_code" }}
        {{- $_ := unset . "store_default_currency_code" }}
        {{- $_ := unset . "store_default_to_transactional_exchange_rate" }}
        {{- $_ := unset . "shipping_address_count" }}
        {{- $_ := unset . "channel_id" }}
        {{- $_ := unset . "ebay_order_id" }}
        {{- $_ := unset . "external_merchant_id" }}
        {{- $_ := unset . "external_source" }}
        {{- $_ := unset . "geoip_country_iso2" }}
        {{- $_ := unset . "handling_cost_ex_tax" }}
        {{- $_ := unset . "handling_cost_inc_tax" }}
        {{- $_ := unset . "subtotal_inc_tax" }}
        {{- $_ := unset . "tax_provider_id" }}
        {{- $_ := unset . "total_ex_tax" }}
        {{- $_ := unset . "wrapping_cost_ex_tax" }}
        {{- $_ := unset . "wrapping_cost_inc_tax" }}
        {{- $_ := unset . "subtotal_tax" }}
        {{- $_ := unset . "subtotal_ex_tax" }}
        {{- $_ := unset . "total_tax" }}
        {{- $_ := unset . "shipping_cost_ex_tax" }}
        {{- $_ := unset . "shipping_cost_inc_tax" }}
        {{- $_ := unset . "discount_amount" }}
        {{- $_ := unset . "coupon_discount" }}
        {{- $_ := unset . "products" }}
        {{- $_ := unset . "coupons" }}
        {{- $_ := unset . "cart_id" }}
        {{- $_ := unset . "store_credit_amount" }}
        {{- $_ := unset . "gift_certificate_amount" }}
        {{- $_ := unset . "customer_message" }}
        {{- $_ := unset . "customer_locale" }}
        {{- $_ := unset . "shipping_addresses" }}
        {{- $_ := unset . "order_source" }}
        {{- $_ := unset . "is_email_opt_in" }}
        {{- $_ := unset . "is_deleted" }}
        {{- $_ := unset . "base_wrapping_cost" }}
        {{- $_ := unset . "external_id" }}
        {{- $_ := unset . "items_shipped" }}
        {{- $_ := unset . "items_total" }}
        {{- $_ := unset . "order_is_digital" }}
        {{- $_ := unset . "external_id"}}
    {{- end -}}
    {{- toJson .rawData -}}
{{- end -}}
