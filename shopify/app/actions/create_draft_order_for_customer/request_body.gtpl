{{- /* Validate required inputs. */ -}}
{{ if or (eq .inputs.customerId nil) (eq .inputs.customerId "") }}
	{{ stop "A customer ID is required to create a draft order for a customer." }}
{{ end }}
{{- $productLineItems := .inputs.productLineItems -}}
{{- $customLineItems := .inputs.customLineItems -}}
{{- $productCount := 0 -}}
{{- if $productLineItems -}}{{- $productCount = len $productLineItems -}}{{- end -}}
{{- $customCount := 0 -}}
{{- if $customLineItems -}}{{- $customCount = len $customLineItems -}}{{- end -}}
{{ if eq (add $productCount $customCount) 0 }}
	{{ stop "At least one line item (product or custom) is required to create a draft order." }}
{{ end }}

{{- /* Build Shopify's single lineItems array. Product variants carry variantId; custom
       items carry title + originalUnitPriceWithCurrency and omit variantId. */ -}}
{{- $lineItems := list -}}
{{- range $productLineItems -}}
  {{- $item := dict "variantId" .variantId "quantity" .quantity -}}
  {{- /* lockPrices: snapshot today's catalog price onto each product line via Shopify's
         generatePriceOverride so it won't recalc to the live price before checkout. Only
         product (variant) lines recalc; custom lines already carry a fixed price. */ -}}
  {{- if $.inputs.lockPrices -}}
    {{- $_ := set $item "generatePriceOverride" true -}}
  {{- end -}}
  {{- if .appliedDiscount -}}
    {{- $_ := set $item "appliedDiscount" .appliedDiscount -}}
  {{- end -}}
  {{- $lineItems = append $lineItems $item -}}
{{- end -}}
{{- range $customLineItems -}}
  {{- $item := dict "title" .title "quantity" .quantity "originalUnitPriceWithCurrency" .originalUnitPrice -}}
  {{- if ne .requiresShipping nil -}}
    {{- $_ := set $item "requiresShipping" .requiresShipping -}}
  {{- end -}}
  {{- if ne .taxable nil -}}
    {{- $_ := set $item "taxable" .taxable -}}
  {{- end -}}
  {{- if .weight -}}
    {{- $_ := set $item "weight" .weight -}}
  {{- end -}}
  {{- if .appliedDiscount -}}
    {{- $_ := set $item "appliedDiscount" .appliedDiscount -}}
  {{- end -}}
  {{- $lineItems = append $lineItems $item -}}
{{- end -}}

{{- $input := dict "lineItems" $lineItems -}}

{{- /* Associate the draft with the customer via Shopify's purchasingEntity (the modern
       replacement for the deprecated DraftOrderInput.customerId). */ -}}
{{- $_ := set $input "purchasingEntity" (dict "customerId" .inputs.customerId) -}}

{{- /* Order-level discount. Field names already match Shopify's
       DraftOrderAppliedDiscountInput, so it passes straight through. */ -}}
{{- if .inputs.appliedDiscount -}}
  {{- $_ := set $input "appliedDiscount" .inputs.appliedDiscount -}}
{{- end -}}

{{- /* Tax exemption for the whole order. Compare against nil (not truthiness) so an
       explicit false is honored. */ -}}
{{- if ne .inputs.taxExempt nil -}}
  {{- $_ := set $input "taxExempt" .inputs.taxExempt -}}
{{- end -}}

{{- /* Optional order note and tags. */ -}}
{{- if .inputs.note -}}
  {{- $_ := set $input "note" .inputs.note -}}
{{- end -}}
{{- if .inputs.tags -}}
  {{- $_ := set $input "tags" .inputs.tags -}}
{{- end -}}

{{- /* Always seed the draft with the customer's default address (sets both shipping and
       billing). The address can be changed later via updateDraftOrder. */ -}}
{{- $_ := set $input "useCustomerDefaultAddress" true -}}

{{- $variables := dict "input" $input -}}

{{ $query := `
mutation DraftOrderCreate($input: DraftOrderInput!) {
  draftOrderCreate(input: $input) {
    draftOrder {
      id
      name
      status
      createdAt
      updatedAt
      invoiceUrl
      invoiceSentAt
      completedAt
      order {
        id
        name
      }
      purchasingEntity {
        __typename
        ... on Customer {
          id
          displayName
          defaultEmailAddress {
            emailAddress
          }
          defaultPhoneNumber {
            phoneNumber
          }
          defaultAddress {
            formatted(withCompany: true)
          }
        }
        ... on PurchasingCompany {
          company {
            id
            name
          }
          contact {
            id
            customer {
              id
              displayName
            }
          }
          location {
            id
            name
          }
        }
      }
      email
      phone
      shippingAddress {
        ...AddressFields
      }
      billingAddress {
        ...AddressFields
      }
      shippingLine {
        title
        discountedPriceSet {
          ...MoneyBagFields
        }
      }
      lineItems(first: 50) {
        pageInfo {
          hasNextPage
        }
        nodes {
          ...LineItemFields
          components {
            id
            name
            quantity
            variant {
              id
              title
            }
          }
        }
      }
      appliedDiscount {
        ...AppliedDiscountFields
      }
      subtotalPriceSet {
        ...MoneyBagFields
      }
      totalShippingPriceSet {
        ...MoneyBagFields
      }
      totalTaxSet {
        ...MoneyBagFields
      }
      totalPriceSet {
        ...MoneyBagFields
      }
      taxesIncluded
      taxExempt
      note2
      tags
      poNumber
      paymentTerms {
        id
        paymentTermsName
        paymentTermsType
        dueInDays
        overdue
      }
    }
    userErrors {
      field
      message
    }
  }
}

fragment LineItemFields on DraftOrderLineItem {
  id
  title
  name
  quantity
  sku
  custom
  requiresShipping
  taxable
  isGiftCard
  weight {
    value
    unit
  }
  originalUnitPriceSet {
    ...MoneyBagFields
  }
  discountedTotalSet {
    ...MoneyBagFields
  }
  appliedDiscount {
    ...AppliedDiscountFields
  }
  variant {
    id
    title
    displayName
    availableForSale
    inventoryQuantity
    inventoryPolicy
  }
}

fragment MoneyBagFields on MoneyBag {
  shopMoney {
    amount
    currencyCode
  }
  presentmentMoney {
    amount
    currencyCode
  }
}

fragment AddressFields on MailingAddress {
  formatted(withCompany: true)
  address1
  address2
  city
  company
  country
  countryCodeV2
  firstName
  lastName
  phone
  province
  provinceCode
  zip
}

fragment AppliedDiscountFields on DraftOrderAppliedDiscount {
  title
  description
  value
  valueType
  amountSet {
    shopMoney {
      amount
      currencyCode
    }
    presentmentMoney {
      amount
      currencyCode
    }
  }
}
` }}

{{- $payload := dict "query" $query "variables" $variables -}}
{{- toJson $payload -}}
