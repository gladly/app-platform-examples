{{- $result := list -}}
{{- $mismatches := list -}}
{{- if .rawData.customers -}}
  {{- range .rawData.customers -}}
    {{- $customer := . -}}
    {{- $includeCustomer := true -}}
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
        {{- $mismatch := printf "Customer name mismatch: Gladly name '%s' does not contain Yotpo first name '%s' or last name '%s'" $gladlyCustomer.name $customer.first_name $customer.last_name -}}
        {{- $mismatches = append $mismatches $mismatch -}}
      {{- end -}}
    {{- end -}}
    
    {{- if $includeCustomer -}}
      {{- $transformedCustomer := dict -}}
      {{- $_ := set $transformedCustomer "external_id" $customer.external_id -}}
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
            {{- $_ := set $list "since" (printf "%sZ" (trimSuffix "Z" .since)) -}}
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
              {{- $_ := set $marketing "timestamp" (printf "%sZ" (trimSuffix "Z" $customer.channels.sms.marketing.timestamp)) -}}
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
              {{- $_ := set $marketing "timestamp" (printf "%sZ" (trimSuffix "Z" $customer.channels.email.marketing.timestamp)) -}}
            {{- end -}}
            {{- if $customer.channels.email.marketing.suppressions -}}
              {{- $suppressions := dict -}}
              {{- $_ := set $suppressions "suppression_reason" $customer.channels.email.marketing.suppressions.suppression_reason -}}
              {{- if $customer.channels.email.marketing.suppressions.timestamp -}}
                {{- $_ := set $suppressions "timestamp" (printf "%sZ" (trimSuffix "Z" $customer.channels.email.marketing.suppressions.timestamp)) -}}
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
          {{- $_ := set $ugc "last_review_date" (printf "%sZ" (trimSuffix "Z" $customer.yotpo_ugc.last_review_date)) -}}
        {{- end -}}
        {{- $_ := set $ugc "last_sentiment" $customer.yotpo_ugc.last_sentiment -}}
        {{- $_ := set $transformedCustomer "yotpo_ugc" $ugc -}}
      {{- end -}}
      {{- $result = append $result $transformedCustomer -}}
    {{- end -}}
  {{- end -}}
{{- end -}}

{{- if eq (len $result) 0 -}}
  {{- $msg := "No customers returned:" -}}
  {{- range $mismatches -}}
    {{- $msg = printf "%s -%s" $msg . -}}
  {{- end -}}
  {{- stop $msg -}}
{{- end -}}

{{- toJson $result -}}
