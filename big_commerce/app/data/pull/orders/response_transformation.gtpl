{{- $externalDateFormat := "Mon, 02 Jan 2006 15:04:05 -0700" -}}
{{- $requireDateFormat := "2006-01-02T15:04:05Z" -}}

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
        {{/* Date values needs to be converted into ISO8601 format in order to be processed by Gladly Sidekick. */}}
        {{- $hasDateCreated := and (ne .date_created "") (ne .date_created nil) -}}
        {{- $hasDateShipped := and (ne .date_shipped "") (ne .date_shipped nil) -}}
        {{- $hasDateModified := and (ne .date_modified "") (ne .date_modified nil) -}}

     
        {{- if $hasDateCreated -}}
            {{- $_ := set . "date_created" (dateInZone $requireDateFormat (toDate $externalDateFormat .date_created) "UTC") -}}
        {{- else }}
            {{- $_ := set . "date_created" nil -}}
        {{- end}}
        
         {{- if $hasDateShipped -}}
            {{- $_ := set . "date_shipped" (dateInZone $requireDateFormat (toDate $externalDateFormat .date_shipped) "UTC") -}}
        {{- else }}
            {{- $_ := set . "date_shipped" nil }}
        {{- end}}

         {{- if $hasDateModified -}}
            {{- $_ := set . "date_modified" (dateInZone $requireDateFormat (toDate $externalDateFormat .date_modified) "UTC") -}}
        {{- else }}
            {{- $_ := set . "date_modified" nil -}}
        {{- end}}

        {{/* Handle conversion of ids to string correctly */}}
        {{- $_ := set . "id" (.id | int64 | toString) -}}
        {{- $_ := set . "customer_id" (.customer_id | int64 | toString) -}}
        {{- $_ := set . "status_id" (.status_id | int64 | toString) -}}

    {{- end -}}
    {{- toJson .rawData -}}
{{- end -}}
