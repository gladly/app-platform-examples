{{- /*
    create_one_time_customer_discount wraps Shopify's discountCodeBasicCreate mutation.

    The action's name is its contract. These values are ALWAYS sent and are NOT inputs:
      - usageLimit                         = 1
      - appliesOncePerCustomer             = true
      - customerGets.appliesOnOneTimePurchase = true
      - discountAmount.appliesOnEachItem   = false  (single amount split across items)

    startsAt is optional: when the caller omits it, the discount starts immediately
    (defaults to the current time) so a make-good discount is usable right away.

    appliesTo is optional: when the caller omits it (or passes an empty object), the
    discount applies to the whole order (items.all = true).
*/ -}}
{{- $a := default (dict) .inputs.appliesTo -}}
{{- $colls := default (list) $a.collectionIds -}}
{{- $prods := default (list) $a.productIds -}}
{{- $variants := default (list) $a.productVariantIds -}}
{{- $explicitAll := eq $a.all true -}}
{{- $hasColls := gt (len $colls) 0 -}}
{{- $hasProds := or (gt (len $prods) 0) (gt (len $variants) 0) -}}

{{- /* Default to the whole order: when no specific target is given (appliesTo omitted
       or empty), the discount applies to all items. */ -}}
{{- $hasAll := or $explicitAll (and (not $hasColls) (not $hasProds)) -}}

{{- /* Targeting all is mutually exclusive with naming specific items */ -}}
{{- if and $explicitAll (or $hasColls $hasProds) -}}
  {{- stop "appliesTo.all cannot be combined with collectionIds, productIds, or productVariantIds." -}}
{{- end -}}

{{- /* Build customerGets.items from the chosen targeting mode */ -}}
{{- $items := dict -}}
{{- if $hasAll -}}
  {{- $_ := set $items "all" true -}}
{{- else -}}
  {{- if $hasColls -}}
    {{- $_ := set $items "collections" (dict "add" $colls) -}}
  {{- end -}}
  {{- if $hasProds -}}
    {{- $products := dict -}}
    {{- if gt (len $prods) 0 -}}
      {{- $_ := set $products "productsToAdd" $prods -}}
    {{- end -}}
    {{- if gt (len $variants) 0 -}}
      {{- $_ := set $products "productVariantsToAdd" $variants -}}
    {{- end -}}
    {{- $_ := set $items "products" $products -}}
  {{- end -}}
{{- end -}}

{{- /* Discount value (fixed-amount off, split across entitled items) */ -}}
{{- $discountAmount := dict "amount" .inputs.discountAmount "appliesOnEachItem" false -}}
{{- $value := dict "discountAmount" $discountAmount -}}
{{- $customerGets := dict "appliesOnOneTimePurchase" true "value" $value "items" $items -}}

{{- /* Scope the discount to the single eligible customer */ -}}
{{- $context := dict "customers" (dict "add" (list .inputs.customerId)) -}}

{{- /* Shopify requires startsAt. Default to "now" so a make-good discount is usable
       immediately; honour an explicit startsAt when the caller provides one. */ -}}
{{- $startsAt := default (dateInZone "2006-01-02T15:04:05Z07:00" now "UTC") .inputs.startsAt -}}

{{- /* Assemble the DiscountCodeBasicInput */ -}}
{{- $basicCodeDiscount := dict
    "title" .inputs.title
    "code" .inputs.code
    "startsAt" $startsAt
    "usageLimit" 1
    "appliesOncePerCustomer" true
    "context" $context
    "customerGets" $customerGets -}}
{{- if .inputs.endsAt -}}
  {{- $_ := set $basicCodeDiscount "endsAt" .inputs.endsAt -}}
{{- end -}}

{{- $variables := dict "basicCodeDiscount" $basicCodeDiscount -}}

{{- $query := `mutation discountCodeBasicCreate($basicCodeDiscount: DiscountCodeBasicInput!) {
  discountCodeBasicCreate(basicCodeDiscount: $basicCodeDiscount) {
    codeDiscountNode {
      id
      codeDiscount {
        ... on DiscountCodeBasic {
          title
          status
          startsAt
          endsAt
          usageLimit
          appliesOncePerCustomer
          context {
            ... on DiscountCustomers {
              customers {
                id
                displayName
              }
            }
          }
          codes(first: 1) {
            nodes {
              code
              asyncUsageCount
            }
          }
          customerGets {
            appliesOnOneTimePurchase
            value {
              ... on DiscountAmount {
                amount {
                  amount
                  currencyCode
                }
                appliesOnEachItem
              }
            }
            items {
              ... on AllDiscountItems {
                allItems
              }
              ... on DiscountCollections {
                collections(first: 10) {
                  nodes {
                    id
                    title
                  }
                }
              }
              ... on DiscountProducts {
                products(first: 10) {
                  nodes {
                    id
                    title
                  }
                }
                productVariants(first: 10) {
                  nodes {
                    id
                    title
                  }
                }
              }
            }
          }
        }
      }
    }
    userErrors {
      field
      code
      message
    }
  }
}` -}}

{{- $payload := dict "query" $query "variables" $variables -}}
{{- toJson $payload -}}
