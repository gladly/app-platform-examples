{{- /* No data is run if customer has no email addresses or phones.
    1. Search for customers matching any of Gladly's customer's emails, for the email we are using exact match
    2. If customer has no emails then use phone numbers. Search for customers matching any of Gladly's customer's phones. For phones we are using exact match.
    */}}
{{- if and (and (ne .customer.emailAddresses nil) (eq (len .customer.emailAddresses) 0)) (and (ne .customer.phoneNumbers nil) (eq (len .customer.phoneNumbers) 0))}}
	{{- stop "unable to retrieve customer data since the customer profile does not have any email addresses and phone number"}}
{{- end}}


{{- $customerQuery := "" }}
{{- if and (ne .customer.emailAddresses nil) (gt (len .customer.emailAddresses) 0) -}}
    {{- range $i, $email := .customer.emailAddresses }}
        {{- if eq $customerQuery "" }}
            {{- $customerQuery = printf "email:'%s'" $email }}
        {{- else }}
            {{- $customerQuery = printf "%s OR email:'%s'" $customerQuery $email }}
        {{- end }}
    {{- end }}
{{- else -}}
    {{- range $i, $phone := .customer.phoneNumbers }}
        {{- if eq $customerQuery "" }}
            {{- $customerQuery = printf "phone:'%s'" $phone.number }}
        {{- else }}
            {{- $customerQuery = printf "%s OR phone:'%s'" $customerQuery $phone.number }}
        {{- end }}
    {{- end }}
{{- end }}


{{$query := printf `
query  {
    customers(first: 2, query: %s) {
        nodes {
            id
            firstName
            lastName
            email
            emailMarketingConsent {
                marketingState
                marketingOptInLevel
                consentUpdatedAt
            }
            smsMarketingConsent {
                marketingState
                marketingOptInLevel
                consentUpdatedAt                    
            }
            lifetimeDuration
            note
            phone
            numberOfOrders
            tags
            amountSpent {
                amount
                currencyCode
            }
            createdAt
            updatedAt
            note
            tags
            lifetimeDuration
            defaultAddress {
                formattedArea
                address1
                address2
                city
                province
                provinceCode
                zip
                country
                countryCode
            }
            addresses {
                address1
                address2
                city
                province
                provinceCode
                zip
                country
                countryCode
            }
            image {
                src
            }
        }
    }
}
` (toJson $customerQuery)}}

{
    "query": {{toJson $query}}
}