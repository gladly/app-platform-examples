{{- $result := list -}}
{{- if .rawData.customers -}}
  {{- range .rawData.customers -}}
    {{- $customer := . -}}
    {{- $includeCustomer := true -}}
    {{- $externalId := "" -}}
    {{- $gladlyCustomer := $.customer -}}
    
    {{- /* Name filtering */ -}}
    {{- if $gladlyCustomer.name -}}
      {{- $gladlyNameLower := lower $gladlyCustomer.name -}}
      {{- $nameMatch := false -}}
      {{- if $customer.first_name -}}
        {{- if contains (lower $customer.first_name) $gladlyNameLower -}}
          {{- $nameMatch = true -}}
        {{- end -}}
      {{- end -}}
      {{- if $customer.last_name -}}
        {{- if contains (lower $customer.last_name) $gladlyNameLower -}}
          {{- $nameMatch = true -}}
        {{- end -}}
      {{- end -}}
      {{- if and (or $customer.first_name $customer.last_name) (not $nameMatch) -}}
        {{- $includeCustomer = false -}}
      {{- end -}}
    {{- end -}}
    
    {{- /* Resolve a stable external ID. Yotpo's external_id is the merchant's ID and is
           null for customers who exist only via UGC/reviews (not tied to a commerce or
           loyalty account), and the core/v3 customer object carries no Yotpo-internal ID.
           Fall back to email (the value the app already links loyalty and reviews on),
           then phone_number, so the record still has a unique, stable ID. If none are
           present, exclude the customer rather than emit an empty External ID, which
           would error the whole data pull.

           Caveat: the external_id is only as stable as the identifier it resolves to. A
           UGC-only customer keyed on email who later gains a merchant external_id (e.g.
           creates a store/loyalty account) will switch keys, so Gladly sees a new record
           rather than an update to the old one. That's an accepted tradeoff for these
           read-only profile cards — better than failing the pull for ID-less shoppers. */ -}}
    {{- if $includeCustomer -}}
      {{- if $customer.external_id -}}
        {{- $externalId = $customer.external_id -}}
      {{- else if $customer.email -}}
        {{- $externalId = $customer.email -}}
      {{- else if $customer.phone_number -}}
        {{- $externalId = $customer.phone_number -}}
      {{- end -}}
      {{- if not $externalId -}}
        {{- $includeCustomer = false -}}
      {{- end -}}
    {{- end -}}

    {{- if $includeCustomer -}}
      {{- $transformedCustomer := dict -}}
      {{- $_ := set $transformedCustomer "external_id" $externalId -}}
      {{- $_ := set $transformedCustomer "email" $customer.email -}}
      {{- $_ := set $transformedCustomer "phone_number" $customer.phone_number -}}
      {{- $_ := set $transformedCustomer "first_name" $customer.first_name -}}
      {{- $_ := set $transformedCustomer "last_name" $customer.last_name -}}
      {{- $_ := set $transformedCustomer "gender" $customer.gender -}}
      {{- if $customer.account_created_at -}}
        {{- $_ := set $transformedCustomer "account_created_at" (printf "%sZ" (trimSuffix "Z" $customer.account_created_at)) -}}
      {{- end -}}
      {{- if $customer.account_updated_at -}}
        {{- $_ := set $transformedCustomer "account_updated_at" (printf "%sZ" (trimSuffix "Z" $customer.account_updated_at)) -}}
      {{- end -}}
      {{- $_ := set $transformedCustomer "account_status" $customer.account_status -}}
      {{- $_ := set $transformedCustomer "default_language" $customer.default_language -}}
      {{- $_ := set $transformedCustomer "default_currency" $customer.default_currency -}}
      {{- if $customer.tags -}}
        {{- $_ := set $transformedCustomer "tags" (join ", " (compact (splitList "," $customer.tags))) -}}
      {{- else -}}
        {{- $_ := set $transformedCustomer "tags" $customer.tags -}}
      {{- end -}}
      {{- if $customer.lists -}}
        {{- $transformedLists := list -}}
        {{- range $customer.lists -}}
          {{- $list := dict -}}
          {{- $_ := set $list "id" .id -}}
          {{- if .since -}}
            {{- template "normalizeDate" (dict "target" $list "field" "since" "value" .since) -}}
          {{- end -}}
          {{- $transformedLists = append $transformedLists $list -}}
        {{- end -}}
        {{- $_ := set $transformedCustomer "lists" $transformedLists -}}
      {{- end -}}
      {{- $_ := set $transformedCustomer "accepts_email_marketing" $customer.accepts_email_marketing -}}
      {{- $_ := set $transformedCustomer "accepts_sms_marketing" $customer.accepts_sms_marketing -}}
      {{- if $customer.channels -}}
        {{- $transformedChannels := dict -}}
        {{- if $customer.channels.sms -}}
          {{- $sms := dict -}}
          {{- if $customer.channels.sms.marketing -}}
            {{- $marketing := dict -}}
            {{- $_ := set $marketing "consent" $customer.channels.sms.marketing.consent -}}
            {{- if $customer.channels.sms.marketing.timestamp -}}
              {{- template "normalizeDate" (dict "target" $marketing "field" "timestamp" "value" $customer.channels.sms.marketing.timestamp) -}}
            {{- end -}}
            {{- $_ := set $sms "marketing" $marketing -}}
          {{- end -}}
          {{- $_ := set $transformedChannels "sms" $sms -}}
        {{- end -}}
        {{- if $customer.channels.email -}}
          {{- $email := dict -}}
          {{- if $customer.channels.email.marketing -}}
            {{- $marketing := dict -}}
            {{- $_ := set $marketing "consent" $customer.channels.email.marketing.consent -}}
            {{- if $customer.channels.email.marketing.timestamp -}}
              {{- template "normalizeDate" (dict "target" $marketing "field" "timestamp" "value" $customer.channels.email.marketing.timestamp) -}}
            {{- end -}}
            {{- if $customer.channels.email.marketing.suppressions -}}
              {{- $suppressions := dict -}}
              {{- $_ := set $suppressions "suppression_reason" $customer.channels.email.marketing.suppressions.suppression_reason -}}
              {{- if $customer.channels.email.marketing.suppressions.timestamp -}}
                {{- template "normalizeDate" (dict "target" $suppressions "field" "timestamp" "value" $customer.channels.email.marketing.suppressions.timestamp) -}}
              {{- end -}}
              {{- $_ := set $marketing "suppressions" $suppressions -}}
            {{- end -}}
            {{- $_ := set $email "marketing" $marketing -}}
          {{- end -}}
          {{- $_ := set $transformedChannels "email" $email -}}
        {{- end -}}
        {{- $_ := set $transformedCustomer "channels" $transformedChannels -}}
      {{- end -}}
      {{- if $customer.yotpo_ugc -}}
        {{- $ugc := dict -}}
        {{- $_ := set $ugc "total_reviews" $customer.yotpo_ugc.total_reviews -}}
        {{- $_ := set $ugc "avg_product_rating" $customer.yotpo_ugc.avg_product_rating -}}
        {{- $_ := set $ugc "avg_site_rating" $customer.yotpo_ugc.avg_site_rating -}}
        {{- $_ := set $ugc "total_avg_rating" $customer.yotpo_ugc.total_avg_rating -}}
        {{- $_ := set $ugc "sentiment_avg_product" $customer.yotpo_ugc.sentiment_avg_product -}}
        {{- $_ := set $ugc "sentiment_avg_site" $customer.yotpo_ugc.sentiment_avg_site -}}
        {{- $_ := set $ugc "total_avg_sentiment" $customer.yotpo_ugc.total_avg_sentiment -}}
        {{- if $customer.yotpo_ugc.top_topics -}}
          {{- $_ := set $ugc "top_topics" (join ", " $customer.yotpo_ugc.top_topics) -}}
        {{- end -}}
        {{- $_ := set $ugc "last_star_rating" $customer.yotpo_ugc.last_star_rating -}}
        {{- if $customer.yotpo_ugc.last_review_date -}}
          {{- template "normalizeDate" (dict "target" $ugc "field" "last_review_date" "value" $customer.yotpo_ugc.last_review_date) -}}
        {{- end -}}
        {{- $_ := set $ugc "last_sentiment" $customer.yotpo_ugc.last_sentiment -}}
        {{- $_ := set $transformedCustomer "yotpo_ugc" $ugc -}}
      {{- end -}}
      {{- $result = append $result $transformedCustomer -}}
    {{- end -}}
  {{- end -}}
{{- end -}}

{{- /* Emit whatever matched — an empty result becomes []. Don't stop on an empty
       result: stop is a TEMPLATE_STOP error that discards every sibling pull, so a
       single unmatched or ID-less shopper would blank the whole Yotpo profile. An
       empty array is a clean success that just shows no customer card. */ -}}
{{- toJson $result -}}
{{- define "normalizeDate" -}}
{{- $hasZone := or (hasSuffix "Z" .value) (regexMatch "[+-][0-9]{2}:[0-9]{2}$" .value) -}}
{{- $parsed := toDate "2006-01-02T15:04:05Z07:00" .value -}}
{{- if $parsed.IsZero -}}{{- $parsed = toDate "2006-01-02T15:04:05" .value -}}{{- end -}}
{{- if $parsed.IsZero -}}{{- $parsed = toDate "2006-01-02" .value -}}{{- end -}}
{{- if $parsed.IsZero -}}{{- $_ := set .target .field nil -}}
{{- else if $hasZone -}}{{- $_ := set .target .field (dateInZone "2006-01-02T15:04:05Z" $parsed "UTC") -}}
{{- else -}}{{- $_ := set .target .field (printf "%sZ" (date "2006-01-02T15:04:05" $parsed)) -}}
{{- end -}}
{{- end -}}
