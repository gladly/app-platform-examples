{{/* Error handling mirrors create_draft_order_for_customer:
    - Non-200 responses result in a 500 (fail)
    - Top-level GraphQL errors are surfaced as `errors`
    - Shopify userErrors (e.g. duplicate code) are surfaced as `userErrors`
*/}}

{{- $errors := .rawData.errors -}}
{{- $hasErrors := or (and $errors (gt (len $errors) 0)) false -}}
{{- $payload := .rawData.data.discountCodeBasicCreate -}}
{{- $userErrors := $payload.userErrors -}}
{{- $hasUserErrors := or (and $userErrors (gt (len $userErrors) 0)) false -}}

{{- if (ne .response.statusCode 200) -}}
    {{- $error := toJson .rawData.errors -}}
    {{- print $error | fail -}}
{{- else if $hasErrors -}}
    {{- $errList := list -}}
    {{- range $errors -}}
        {{- $errList = append $errList (dict "message" .message "code" .extensions.code) -}}
    {{- end -}}
    {{- toJson (dict "discountCode" nil "errors" $errList "userErrors" (list)) -}}
{{- else if $hasUserErrors -}}
    {{- $ueList := list -}}
    {{- range $userErrors -}}
        {{- $ueList = append $ueList (dict "message" .message "field" .field) -}}
    {{- end -}}
    {{- toJson (dict "discountCode" nil "errors" (list) "userErrors" $ueList) -}}
{{- else -}}
    {{- /* The raw codeDiscount is already DiscountCodeBasic-shaped, so we pass it through
           and only flatten the connections Shopify returns (`codes`, and the `items`
           collection/product connections) from `{nodes: [...]}` to plain lists. `value`
           and the scalar fields (title/status/startsAt/...) pass through untouched. */ -}}
    {{- $node := $payload.codeDiscountNode -}}
    {{- $cd := $node.codeDiscount -}}
    {{- $cg := $cd.customerGets -}}

    {{- /* items: flatten connection `nodes` to plain lists */ -}}
    {{- $items := dict -}}
    {{- $rawItems := $cg.items -}}
    {{- if $rawItems -}}
        {{- if hasKey $rawItems "allItems" -}}{{- $_ := set $items "allItems" $rawItems.allItems -}}{{- end -}}
        {{- if $rawItems.collections -}}{{- $_ := set $items "collections" $rawItems.collections.nodes -}}{{- end -}}
        {{- if $rawItems.products -}}{{- $_ := set $items "products" $rawItems.products.nodes -}}{{- end -}}
        {{- if $rawItems.productVariants -}}{{- $_ := set $items "productVariants" $rawItems.productVariants.nodes -}}{{- end -}}
    {{- end -}}

    {{- /* Reshape $cd in place: flatten codes + items to lists and stamp the node id. */ -}}
    {{- if $cd.codes -}}{{- $_ := set $cd "codes" $cd.codes.nodes -}}{{- end -}}
    {{- $_ := set $cg "items" $items -}}
    {{- $_ := set $cd "id" $node.id -}}

    {{- toJson (dict "discountCode" $cd "errors" (list) "userErrors" (list)) -}}
{{- end -}}
