{{- $result := list -}}
{{- if .rawData.reviews -}}
{{- if .rawData.reviews.reviews -}}
{{- range .rawData.reviews.reviews -}}
{{- $review := . -}}
{{- $transformedReview := dict -}}
{{- $_ := set $transformedReview "id" .id -}}
{{- $_ := set $transformedReview "score" .score -}}
{{- $_ := set $transformedReview "votes_up" .votes_up -}}
{{- $_ := set $transformedReview "votes_down" .votes_down -}}
{{- template "decodeHtml" (dict "target" $transformedReview "field" "content" "value" .content) -}}
{{- template "decodeHtml" (dict "target" $transformedReview "field" "title" "value" .title) -}}
{{- if .created_at -}}
{{- $_ := set $transformedReview "created_at" (printf "%sZ" (trimSuffix "Z" .created_at)) -}}
{{- end -}}
{{- $_ := set $transformedReview "verified_buyer" .verified_buyer -}}
{{- $_ := set $transformedReview "source_review_id" .source_review_id -}}
{{- $images := list -}}
{{- if .images_data -}}
{{- range .images_data -}}
{{- $img := dict "thumb_url" .thumb_url "original_url" .original_url -}}
{{- $images = append $images $img -}}
{{- end -}}
{{- end -}}
{{- $_ := set $transformedReview "images_data" $images -}}
{{- if .user -}}
{{- $_ := set .user "id" (printf "%.0f" .user.id) -}}
{{- end -}}
{{- $_ := set $transformedReview "user" .user -}}
{{- if .product -}}
{{- $_ := set .product "id" (printf "%.0f" .product.id) -}}
{{- end -}}
{{- $_ := set $transformedReview "product" .product -}}
{{- $_ := set $transformedReview "user_id" (printf "%.0f" .user_id) -}}
{{- $_ := set $transformedReview "product_id" (printf "%.0f" .product_id) -}}
{{- if .comment -}}
{{- $comment := .comment -}}
{{- if $comment.content -}}
{{- template "decodeHtml" (dict "target" $comment "field" "content" "value" $comment.content) -}}
{{- end -}}
{{- $_ := set $transformedReview "comment" $comment -}}
{{- else -}}
{{- $_ := set $transformedReview "comment" .comment -}}
{{- end -}}
{{- $_ := set $transformedReview "external_product_id" .external_product_id -}}
{{- $_ := set $transformedReview "published" .published -}}
{{- $_ := set $transformedReview "order_id" .order_id -}}
{{- $_ := set $transformedReview "external_order_id" .external_order_id -}}
{{- $_ := set $transformedReview "is_incentivized" .is_incentivized -}}
{{- $_ := set $transformedReview "incentive_type" .incentive_type -}}
{{- $customFields := list -}}
{{- if .custom_fields -}}
{{- range $key, $val := .custom_fields -}}
{{- $cf := dict "title" $val.title "value" (printf "%v" $val.value) "field_type" $val.field_type -}}
{{- $customFields = append $customFields $cf -}}
{{- end -}}
{{- end -}}
{{- $_ := set $transformedReview "custom_fields" $customFields -}}
{{- $result = append $result $transformedReview -}}
{{- end -}}
{{- end -}}
{{- end -}}
{{- toJson $result -}}
{{- define "decodeHtml" -}}
{{- if .value -}}
{{- $_ := set .target .field (.value | replace "&#x27;" "'" | replace "&#39;" "'" | replace "&quot;" "\"" | replace "&amp;" "&" | replace "&lt;" "<" | replace "&gt;" ">") -}}
{{- else -}}
{{- $_ := set .target .field .value -}}
{{- end -}}
{{- end -}}