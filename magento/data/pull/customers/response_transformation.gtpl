{{- if not .rawData.items -}}
[]
{{- else -}}
{{- $results := list -}}
{{- range .rawData.items -}}
    {{- $customer := dict
        "customerId" (toString (int64 .id))
        "email" .email
        "firstName" .firstname
        "lastName" .lastname
        "createdAt" (dateInZone "2006-01-02T15:04:05Z" (printf "%s UTC" .created_at | toDate "2006-01-02 15:04:05 MST") "UTC")
        "updatedAt" (dateInZone "2006-01-02T15:04:05Z" (printf "%s UTC" .updated_at | toDate "2006-01-02 15:04:05 MST") "UTC")
    -}}
    {{- range .addresses -}}
        {{- if .default_billing -}}
            {{- if .street -}}
                {{- $_ := set $customer "street" (first .street) -}}
            {{- end -}}
            {{- if .city -}}
                {{- $_ := set $customer "city" .city -}}
            {{- end -}}
            {{- if .region -}}
                {{- $_ := set $customer "region" .region.region -}}
            {{- end -}}
            {{- if .postcode -}}
                {{- $_ := set $customer "postCode" .postcode -}}
            {{- end -}}
            {{- if .country_id -}}
                {{- $_ := set $customer "countryCode" .country_id -}}
            {{- end -}}
            {{- if .telephone -}}
                {{- $_ := set $customer "telephone" .telephone -}}
            {{- end -}}
        {{- end -}}
    {{- end -}}
    {{- $results = append $results $customer -}}
{{- end -}}
{{- toJson $results -}}
{{- end -}}