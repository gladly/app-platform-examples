{{/*
    Check if the response is empty (204 No Content)
    - The purpose of this check is to handle the scenario where no shipments are found for the given order.
    - If the response status code is 204, it means no shipment exists.
    - An empty array is returned since shipments for orders may not always be created until the shipment is created.
*/}}
{{- if eq .response.statusCode 204 }}
[]
{{- else }}
    {{/*
        Iterate over all shipments.
        - This loop processes each shipment record individually.
    */}}
    {{- range .rawData -}}
        {{/* date_created value needs to be converted into ISO8601 format in order to be processed by Gladly Sidekick. */}}
        {{- if and (ne .date_created "") (ne .date_created nil) }}
            {{- $time := "" }}
                {{- $time = toDate "Mon, 02 Jan 2006 15:04:05 -0700" .date_created }}
                {{- $_ := set . "date_created" ($time.Format "2006-01-02T15:04:05Z") }}
        {{- else }}
            {{- $_ := set . "date_created" nil }}
        {{- end}}
        {{/*
            Remove key value pairs from the data because they are not needed.
            - This step cleans up unnecessary data before further processing.
        */}}
        {{- $_ := unset . "merchant_shipping_cost"}}
        {{- $_ := unset . "comments"}}
        {{- $_ := unset . "shipping_provider_display_name"}}
        {{- $_ := unset . "order_address_id"}}
    {{- end -}}
    {{- toJson .rawData -}}
{{- end }}
