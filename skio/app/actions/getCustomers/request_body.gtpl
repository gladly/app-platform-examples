{{/*
  This template builds a GraphQL query to fetch customers by email or phone, including their subscriptions.
  Inputs: .inputs.email (optional), .inputs.phone (optional)
*/}}

{{$conditions := list}}
{{if .inputs.email}}
  {{$conditions = append $conditions (printf "{email: {_eq: \"%s\"}}" .inputs.email)}}
{{end}}
{{if .inputs.phone}}
  {{$conditions = append $conditions (printf "{phoneNumber: {_eq: \"%s\"}}" .inputs.phone)}}
{{end}}

{{$query := printf "\nquery {\n  StorefrontUsers(where: {_or: [%s]}) {\n    id\n    email\n    firstName\n    lastName\n    phoneNumber\n    platformId\n    createdAt\n    updatedAt\n    Subscriptions {\n      id\n      status\n      createdAt\n      updatedAt\n      storefrontUserId\n      BillingPolicy {\n        id\n        interval\n        intervalCount\n      }\n      DeliveryPolicy {\n        id\n        interval\n        intervalCount\n      }\n    }\n  }\n}\n" (join ", " $conditions)}}

{
  "query": {{toJson $query}}
} 