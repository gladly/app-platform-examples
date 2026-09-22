{{- /* Validate required inputs */ -}}
{{ if or (eq .inputs.draftOrderId nil) (eq .inputs.draftOrderId "") }}
	{{ stop "Draft Order ID is required."}}
{{ end }}

{{- /* Default sourceName to "gladly" when not provided, so resulting orders are
       attributed to the Gladly sales channel unless the caller overrides it. */ -}}
{{- $sourceName := "gladly" -}}
{{- if .inputs.sourceName -}}
	{{- $sourceName = .inputs.sourceName -}}
{{- end -}}

{{- /* Default paymentPending to true: the resulting order starts as PENDING and
       payment is collected later. Compare against nil (not truthiness) so an
       explicit `false` input overrides the default. Shopify deprecates the
       draftOrderComplete `paymentPending` argument in favor of draft order
       payment terms; we use it deliberately as a simple paid/pending toggle. */ -}}
{{- $paymentPending := true -}}
{{- if ne .inputs.paymentPending nil -}}
	{{- $paymentPending = .inputs.paymentPending -}}
{{- end -}}

{{- $variables := dict "id" .inputs.draftOrderId "sourceName" $sourceName "paymentPending" $paymentPending -}}

{{ $query := `
mutation CompleteDraftOrder($id: ID!, $sourceName: String, $paymentPending: Boolean) {
  draftOrderComplete(id: $id, sourceName: $sourceName, paymentPending: $paymentPending) {
    draftOrder {
      id
      name
      status
      order {
        id
        name
        createdAt
        displayFinancialStatus
        displayFulfillmentStatus
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
        shippingAddress {
          ...AddressFields
        }
        lineItems(first: 50) {
          pageInfo {
            hasNextPage
          }
          nodes {
            id
            name
            quantity
            originalUnitPriceSet {
              ...MoneyBagFields
            }
            discountedTotalSet {
              ...MoneyBagFields
            }
          }
        }
        subtotalPriceSet {
          ...MoneyBagFields
        }
        totalDiscountsSet {
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
        note
        paymentTerms {
          id
          paymentTermsName
          paymentTermsType
          dueInDays
          overdue
        }
      }
    }
    userErrors {
      field
      message
    }
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
` }}

{{- $payload := dict "query" $query "variables" $variables -}}
{{- toJson $payload -}}
