{{- /* Updates only the basic fields provided (email, note, phone, poNumber, shippingAddress)
       on an existing draft order. Omitted fields are left untouched. Line items, discounts,
       tax settings, and the customer are NOT changed by this action.

       customerId is required but is NOT sent to Shopify (draftOrderUpdate has no customer
       argument). It is verified against the returned draft's purchasingEntity in
       response_transformation.gtpl, which adds a non-fatal alert on mismatch. */ -}}

{{- /* Validate required inputs. */ -}}
{{ if or (eq .inputs.draftOrderId nil) (eq .inputs.draftOrderId "") }}
	{{ stop "A draft order ID is required to update a draft order." }}
{{ end }}
{{ if or (eq .inputs.customerId nil) (eq .inputs.customerId "") }}
	{{ stop "A customer ID is required to update a draft order." }}
{{ end }}

{{- /* Build the input from only the provided updatable fields. Compare with `ne nil` (not
       truthiness) so an explicit empty string can intentionally clear a field. */ -}}
{{- $input := dict -}}
{{- if ne .inputs.email nil -}}
  {{- $_ := set $input "email" .inputs.email -}}
{{- end -}}
{{- if ne .inputs.note nil -}}
  {{- $_ := set $input "note" .inputs.note -}}
{{- end -}}
{{- if ne .inputs.phone nil -}}
  {{- $_ := set $input "phone" .inputs.phone -}}
{{- end -}}
{{- if ne .inputs.poNumber nil -}}
  {{- $_ := set $input "poNumber" .inputs.poNumber -}}
{{- end -}}

{{- /* Build the nested shippingAddress from only the provided sub-fields. The input field
       names already match Shopify's MailingAddressInput (countryCode/provinceCode carry ISO
       codes as strings), so each provided sub-field passes straight through. */ -}}
{{- if .inputs.shippingAddress -}}
  {{- $addr := dict -}}
  {{- range $field, $value := .inputs.shippingAddress -}}
    {{- if ne $value nil -}}
      {{- $_ := set $addr $field $value -}}
    {{- end -}}
  {{- end -}}
  {{- if gt (len $addr) 0 -}}
    {{- $_ := set $input "shippingAddress" $addr -}}
  {{- end -}}
{{- end -}}

{{- /* Reject a no-op: at least one updatable field must be present. */ -}}
{{ if eq (len $input) 0 }}
	{{ stop "At least one field to update is required (email, note, phone, poNumber, or shippingAddress)." }}
{{ end }}

{{- $variables := dict "id" .inputs.draftOrderId "input" $input -}}

{{ $query := `
mutation DraftOrderUpdate($id: ID!, $input: DraftOrderInput!) {
  draftOrderUpdate(id: $id, input: $input) {
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
