{{/* Error handling:
    - Non-200 responses result in a 500 (fail)
    - Top-level GraphQL errors are surfaced as `errors`
    - Shopify userErrors are surfaced as `userErrors`
*/}}

{{- /* When Shopify reports top-level errors the body carries `"data": null`, so
       `.rawData.data.draftOrderComplete` must only be dereferenced after the
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
    {{- toJson (dict "order" nil "draftOrder" nil "errors" $errList "userErrors" (list)) -}}
{{- else -}}
{{- $payload := .rawData.data.draftOrderComplete -}}
{{- $userErrors := $payload.userErrors -}}
{{- $hasUserErrors := or (and $userErrors (gt (len $userErrors) 0)) false -}}
{{- if $hasUserErrors -}}
    {{- $ueList := list -}}
    {{- range $userErrors -}}
        {{- $ueList = append $ueList (dict "message" .message "field" .field) -}}
    {{- end -}}
    {{- toJson (dict "order" nil "draftOrder" nil "errors" (list) "userErrors" $ueList) -}}
{{- else -}}
    {{- /* The interesting payload is the order created from the draft; the draft
           order itself is reduced to a minimal {id, name, status} ref. The raw
           order needs three reshapes:
           - flatten the lineItems connection from {nodes: [...]} to a plain list
           - rename displayFinancialStatus/displayFulfillmentStatus to
             financialStatus/fulfillmentStatus
           - resolve the purchasingEntity union by __typename into {customer} or {company},
             mapping the Customer's defaultEmailAddress/defaultPhoneNumber to flat email/phone */ -}}
    {{- $do := $payload.draftOrder -}}
    {{- $draftOrderRef := dict "id" $do.id "name" $do.name "status" $do.status -}}
    {{- $order := $do.order -}}

    {{- /* Shopify can return the draft order without an order (e.g. while the
           order is still being created); skip the reshaping so `"order": null`
           passes through with the draftOrder ref intact. */ -}}
    {{- if $order -}}

    {{- if $order.lineItems -}}
        {{- /* Surface the truncation signal before the connection wrapper is
               flattened away: hasMoreLineItems tells the agent the order has
               more lines than the first 50 returned. */ -}}
        {{- if $order.lineItems.pageInfo -}}
            {{- $_ := set $order "hasMoreLineItems" $order.lineItems.pageInfo.hasNextPage -}}
        {{- end -}}
        {{- /* Flatten the lineItems connection to a plain list. Each node already
               carries originalUnitPriceSet (unit price) and discountedTotalSet
               (row total after discounts, before taxes) as Shopify's own currency
               values — no app-side computation, so any currency's precision is
               preserved as Shopify returned it. */ -}}
        {{- $_ := set $order "lineItems" $order.lineItems.nodes -}}
    {{- end -}}

    {{- $_ := set $order "financialStatus" $order.displayFinancialStatus -}}
    {{- $_ := unset $order "displayFinancialStatus" -}}

    {{- $_ := set $order "fulfillmentStatus" $order.displayFulfillmentStatus -}}
    {{- $_ := unset $order "displayFulfillmentStatus" -}}

    {{- if $order.purchasingEntity -}}
        {{- $pe := $order.purchasingEntity -}}
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
            {{- $_ := set $order "purchasingEntity" (dict "customer" $customer) -}}
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
            {{- $_ := set $order "purchasingEntity" (dict "company" $company) -}}
        {{- end -}}
    {{- end -}}

    {{- end -}}

    {{- toJson (dict "order" $order "draftOrder" $draftOrderRef "errors" (list) "userErrors" (list)) -}}
{{- end -}}
{{- end -}}
