{{/* Error handling mirrors create_draft_order_from_order:
    - Non-200 responses result in a 500 (fail)
    - Top-level GraphQL errors are surfaced as `errors`
    - Shopify userErrors are surfaced as `userErrors`
*/}}

{{- /* When Shopify reports top-level errors the body carries `"data": null`, so
       `.rawData.data.draftOrderCreate` must only be dereferenced after the
       status and top-level error checks — earlier access crashes the template. */ -}}
{{- $errors := .rawData.errors -}}
{{- $hasErrors := or (and $errors (gt (len $errors) 0)) false -}}

{{- if (ne .response.statusCode 200) -}}
    {{- /* Include the status code, and don't assume the error body carries an
           `errors` key — a proxy or HTML error page wouldn't, and `toJson nil`
           would otherwise degrade the message to a bare "null". */ -}}
    {{- if .rawData.errors -}}
        {{- fail (printf "Shopify responded with HTTP %v: %s" .response.statusCode (toJson .rawData.errors)) -}}
    {{- else -}}
        {{- fail (printf "Shopify responded with HTTP %v" .response.statusCode) -}}
    {{- end -}}
{{- else if $hasErrors -}}
    {{- $errList := list -}}
    {{- range $errors -}}
        {{- $errList = append $errList (dict "message" .message "code" .extensions.code) -}}
    {{- end -}}
    {{- toJson (dict "draftOrder" nil "errors" $errList "userErrors" (list)) -}}
{{- else -}}
{{- $payload := .rawData.data.draftOrderCreate -}}
{{- $userErrors := $payload.userErrors -}}
{{- $hasUserErrors := or (and $userErrors (gt (len $userErrors) 0)) false -}}
{{- if $hasUserErrors -}}
    {{- $ueList := list -}}
    {{- range $userErrors -}}
        {{- $ueList = append $ueList (dict "message" .message "field" .field) -}}
    {{- end -}}
    {{- toJson (dict "draftOrder" nil "errors" (list) "userErrors" $ueList) -}}
{{- else -}}
    {{- /* The raw draft order is already DraftOrder-shaped (MoneyBags, variant, weight,
           tax lines all pass through verbatim). Three reshapes are needed:
           - partition the lineItems connection into productLineItems / customLineItems
             by the raw `custom` flag, emitting only the fields each type declares
           - rename the legacy `note2` field to `note`
           - resolve the purchasingEntity union by __typename into {customer} or {company},
             mapping the Customer's defaultEmailAddress/defaultPhoneNumber to flat email/phone */ -}}
    {{- $draftOrder := $payload.draftOrder -}}

    {{- /* Partition line items: Shopify returns one connection mixing catalog (custom=false)
           and custom (custom=true) lines. Split them into the two schema lists, building each
           item from only the fields its type declares (DraftOrderProductLineItem /
           DraftOrderCustomLineItem). */ -}}
    {{- $productLineItems := list -}}
    {{- $customLineItems := list -}}
    {{- if $draftOrder.lineItems -}}
        {{- /* Surface the truncation signal before the connection wrapper is dropped:
               hasMoreLineItems tells the agent there are more lines than the first 50. */ -}}
        {{- if $draftOrder.lineItems.pageInfo -}}
            {{- $_ := set $draftOrder "hasMoreLineItems" $draftOrder.lineItems.pageInfo.hasNextPage -}}
        {{- end -}}
        {{- range $li := $draftOrder.lineItems.nodes -}}
            {{- if $li.custom -}}
                {{- $customLineItems = append $customLineItems (dict
                    "id" $li.id
                    "title" $li.title
                    "quantity" $li.quantity
                    "requiresShipping" $li.requiresShipping
                    "taxable" $li.taxable
                    "weight" $li.weight
                    "originalUnitPriceSet" $li.originalUnitPriceSet
                    "discountedTotalSet" $li.discountedTotalSet
                    "appliedDiscount" $li.appliedDiscount) -}}
            {{- else -}}
                {{- $productLineItems = append $productLineItems (dict
                    "id" $li.id
                    "name" $li.name
                    "quantity" $li.quantity
                    "variant" $li.variant
                    "sku" $li.sku
                    "components" $li.components
                    "requiresShipping" $li.requiresShipping
                    "taxable" $li.taxable
                    "isGiftCard" $li.isGiftCard
                    "originalUnitPriceSet" $li.originalUnitPriceSet
                    "discountedTotalSet" $li.discountedTotalSet
                    "appliedDiscount" $li.appliedDiscount) -}}
            {{- end -}}
        {{- end -}}
    {{- end -}}
    {{- $_ := unset $draftOrder "lineItems" -}}
    {{- $_ := set $draftOrder "productLineItems" $productLineItems -}}
    {{- $_ := set $draftOrder "customLineItems" $customLineItems -}}

    {{- if hasKey $draftOrder "note2" -}}
        {{- $_ := set $draftOrder "note" $draftOrder.note2 -}}
        {{- $_ := unset $draftOrder "note2" -}}
    {{- end -}}

    {{- if $draftOrder.purchasingEntity -}}
        {{- $pe := $draftOrder.purchasingEntity -}}
        {{- if eq $pe.__typename "Customer" -}}
            {{- $customer := dict "id" $pe.id "displayName" $pe.displayName -}}
            {{- if $pe.defaultEmailAddress -}}
                {{- $_ := set $customer "email" $pe.defaultEmailAddress.emailAddress -}}
            {{- end -}}
            {{- if $pe.defaultPhoneNumber -}}
                {{- $_ := set $customer "phone" $pe.defaultPhoneNumber.phoneNumber -}}
            {{- end -}}
            {{- if $pe.defaultAddress -}}
                {{- $_ := set $customer "defaultAddressFormatted" $pe.defaultAddress.formatted -}}
            {{- end -}}
            {{- $_ := set $draftOrder "purchasingEntity" (dict "customer" $customer) -}}
        {{- else if eq $pe.__typename "PurchasingCompany" -}}
            {{- $company := dict "company" $pe.company "location" $pe.location -}}
            {{- if $pe.contact -}}
                {{- $contact := dict "id" $pe.contact.id -}}
                {{- if $pe.contact.customer -}}
                    {{- $cc := $pe.contact.customer -}}
                    {{- $_ := set $contact "customer" (dict "id" $cc.id "displayName" $cc.displayName) -}}
                {{- end -}}
                {{- $_ := set $company "contact" $contact -}}
            {{- end -}}
            {{- $_ := set $draftOrder "purchasingEntity" (dict "company" $company) -}}
        {{- end -}}
    {{- end -}}

    {{- toJson (dict "draftOrder" $draftOrder "errors" (list) "userErrors" (list)) -}}
{{- end -}}
{{- end -}}
