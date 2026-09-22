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

        {{/* Handle conversion of ids to string correctly */}}
        {{- $_ := set . "id" (.id | int64 | toString) -}}
        {{- $_ := set . "order_id" (.order_id | int64 | toString) -}}
        {{- $_ := set . "customer_id" (.customer_id | int64 | toString) -}}
        {{- range .items -}}
        {{- $_ := set . "order_product_id" (.order_product_id | int64 | toString) -}}
        {{- $_ := set . "product_id" (.product_id | int64 | toString) -}}
        {{end}}

    {{- end -}}
    {{- toJson .rawData -}}
{{- end }}
