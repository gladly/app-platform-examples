{{- /* Build static standard and all cancellation reasons */ -}}
{{- $standardReasons := list -}}
{{- $allReasons := list -}}
{{- $merchantReasons := list -}}

{{- /* Standard reasons (from expected table) */ -}}
{{- $standardReasons = list
  (dict "code" "1"  "description" "Customer Request")
  (dict "code" "2"  "description" "Fraud")
  (dict "code" "3"  "description" "Payment Issues")
  (dict "code" "4"  "description" "Overstocked")
  (dict "code" "5"  "description" "Out of Stock")
  (dict "code" "6"  "description" "Quality Issues")
  (dict "code" "7"  "description" "Shipping Issues")
  (dict "code" "8"  "description" "Duplicate Order")
  (dict "code" "9"  "description" "Wrong Product")
  (dict "code" "10" "description" "Other")
-}}

{{- /* All cancellation reasons (from expected table) */ -}}
{{- $allReasons = list
  (dict "code" "1"  "description" "Customer Request")
  (dict "code" "2"  "description" "Fraud")
  (dict "code" "3"  "description" "Payment Issues")
  (dict "code" "4"  "description" "Overstocked")
  (dict "code" "5"  "description" "Out of Stock")
  (dict "code" "6"  "description" "Quality Issues")
  (dict "code" "7"  "description" "Shipping Issues")
  (dict "code" "8"  "description" "Duplicate Order")
  (dict "code" "9"  "description" "Wrong Product")
  (dict "code" "10" "description" "Other")
  (dict "code" "11" "description" "Product Discontinued")
  (dict "code" "12" "description" "Seasonal Product")
  (dict "code" "13" "description" "Price Increase")
  (dict "code" "14" "description" "Customer Moved")
  (dict "code" "15" "description" "Customer Deceased")
  (dict "code" "16" "description" "Customer Unhappy")
  (dict "code" "17" "description" "Product Not as Expected")
  (dict "code" "18" "description" "Too Expensive")
  (dict "code" "19" "description" "Found Better Alternative")
  (dict "code" "20" "description" "No Longer Needed")
  (dict "code" "21" "description" "Gift Subscription Ended")
  (dict "code" "22" "description" "Trial Period Ended")
  (dict "code" "23" "description" "Customer Service Issues")
  (dict "code" "24" "description" "Website Issues")
  (dict "code" "25" "description" "Technical Problems")
  (dict "code" "26" "description" "Billing Issues")
  (dict "code" "27" "description" "Credit Card Expired")
  (dict "code" "28" "description" "Insufficient Funds")
  (dict "code" "29" "description" "Bank Declined")
  (dict "code" "30" "description" "Payment Method Changed")
  (dict "code" "31" "description" "Account Closed")
  (dict "code" "32" "description" "Identity Verification Failed")
  (dict "code" "33" "description" "Suspicious Activity")
  (dict "code" "34" "description" "Test Order")
  (dict "code" "35" "description" "Accidental Order")
  (dict "code" "36" "description" "Wrong Address")
  (dict "code" "37" "description" "Address Unverifiable")
  (dict "code" "38" "description" "International Shipping Issues")
  (dict "code" "39" "description" "Shipping Too Expensive")
  (dict "code" "40" "description" "Shipping Too Slow")
  (dict "code" "41" "description" "Damaged in Transit")
  (dict "code" "42" "description" "Lost in Transit")
  (dict "code" "43" "description" "Delivered to Wrong Address")
  (dict "code" "44" "description" "Returned to Sender")
  (dict "code" "45" "description" "Weather Delay")
  (dict "code" "46" "description" "Natural Disaster")
  (dict "code" "47" "description" "Pandemic Related")
  (dict "code" "48" "description" "Supply Chain Issues")
  (dict "code" "49" "description" "Manufacturing Delay")
  (dict "code" "50" "description" "Vendor Issues")
  (dict "code" "51" "description" "Inventory Management Error")
  (dict "code" "52" "description" "System Error")
  (dict "code" "53" "description" "Staff Error")
  (dict "code" "54" "description" "Data Entry Error")
  (dict "code" "55" "description" "Processing Error")
  (dict "code" "56" "description" "Website Error")
  (dict "code" "57" "description" "App Error")
  (dict "code" "58" "description" "Integration Error")
  (dict "code" "59" "description" "API Error")
  (dict "code" "60" "description" "Database Error")
-}}

{
  "standard_cancellation_reasons": [
{{- range $index, $reason := $standardReasons -}}
{{- if $index -}},{{- end }}
    {
      "code": "{{$reason.code}}",
      "description": "{{$reason.description}}"
    }
{{- end -}}
  ],
  "all_cancellation_reasons": [
{{- range $index, $reason := $allReasons -}}
{{- if $index -}},{{- end }}
    {
      "code": "{{$reason.code}}",
      "description": "{{$reason.description}}"
    }
{{- end -}}
  ],
  "merchant_cancellation_reasons": [
{{- range $index, $reason := .rawData.results -}}
{{- if $index -}},{{- end }}
    {
      "cancel_reason": {{$reason.cancel_reason}},
      "application": "{{$reason.application}}"
    }
{{- end -}}
  ]
}
