{{/*
    Check if the response is empty (204 No Content) or if the status code 404 is received.
    - The purpose of this check is to handle the scenario where no transactions are found for the given order.
    - If the response status code is 204 or 404, it means no transaction exists.
    - Transactions are needed for an order, so processing will stop if a transaction isn't found for an order.
*/}}
{{- if or (eq .response.statusCode 404) (eq .response.statusCode 204) }}
    {{stop "No transactions found for BigCommerce order."}}
{{- else if or (eq .rawData nil) (eq .rawData.data nil) }}
    {{stop "No transactions found for BigCommerce order."}}
{{- else }}
      {{/*
        Iterate over all transactions.
        - This loop processes each transaction record individually.
    */}}
    {{- range .rawData.data -}}
        {{/* date_created value needs to be converted into ISO8601 format in order to be processed by Gladly Sidekick. */}}
         {{- if and (ne .date_created "") (ne .date_created nil) }}
            {{- $time := "" }}
            {{- if regexMatch "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\+\\d{2}:\\d{2}" .date_created }}
                {{- $time = toDate "2006-01-02T15:04:05-07:00" .date_created }}
                {{- $_ := set . "date_created" ($time.Format "2006-01-02T15:04:05Z") }}
            {{- end}}
        {{- else }}
             {{- $_ := set . "date_created" nil }}
        {{- end}}


        {{/* Set currency type. */}}
        {{- $_ := set . "currency" (default "USD" .currency) }}

        {{/*
            Remove key value pairs from the data because they are not needed.
            - This step cleans up unnecessary data before further processing.
        */}}
        {{- $_ := unset . "custom_provider_field_result" }}
        {{- $_ := unset . "payment_instrument_token" }}
        {{- $_ := unset . "offline" }}
        {{- $_ := unset . "custom" }}
        {{- $_ := unset . "payment_method_id" }}
        {{- $_ := unset . "avs_result" }}
        {{- $_ := unset . "cvv_result" }}
        {{- $_ := unset . "credit_card" }}
        {{- $_ := unset . "gift_certificate" }}
        {{- $_ := unset . "method" }}
        {{- $_ := unset . "gateway_transaction_id" }}
        {{- $_ := unset . "test" }}
        {{- $_ := unset . "reference_transaction_id" }}
        {{- $_ := unset . "fraud_review" }}
        {{- $_ := unset . "store_credit" }}
    {{- end -}}
    {{- toJson .rawData.data -}}
{{- end -}}
