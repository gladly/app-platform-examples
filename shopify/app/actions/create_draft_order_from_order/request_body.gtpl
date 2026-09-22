{{- /* Validate required inputs. customerId is required so the resulting draft is always
       tied to a customer; it is not sent to Shopify (draftOrderCreateFromOrder only accepts
       orderId) but is verified against the duplicated draft in the response transformation. */ -}}
{{ if or (eq .inputs.orderId nil) (eq .inputs.orderId "") }}
	{{ stop "An order ID is required to create a draft order from an order." }}
{{ end }}
{{ if or (eq .inputs.customerId nil) (eq .inputs.customerId "") }}
	{{ stop "A customer ID is required to create a draft order from an order." }}
{{ end }}

{{- $variables := dict "orderId" .inputs.orderId -}}

{{ $query := `
mutation DraftOrderCreateFromOrder($orderId: ID!) {
  draftOrderCreateFromOrder(orderId: $orderId) {
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
