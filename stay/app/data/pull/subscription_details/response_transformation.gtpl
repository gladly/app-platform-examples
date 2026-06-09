{{- if not (kindIs "map" .rawData) -}}
    {{- fail "unexpected Stay.ai subscription detail response shape (expected a JSON object)" -}}
{{- end -}}
{{- $d := .rawData -}}
{{- if eq $d.id nil -}}
    {{- fail "Stay.ai subscription detail response is missing the subscription id" -}}
{{- end -}}

{{- /* IDs may arrive as JSON numbers (decoded float64) or strings; normalize to strings */ -}}
{{- $id := kindIs "float64" $d.id | ternary ($d.id | int64 | toString) ($d.id | toString) -}}

{{- /* the detail endpoint nests status as {status, pausedUntil}; flatten it */ -}}
{{- $status := $d.status -}}
{{- $pausedUntil := $d.pausedUntil -}}
{{- if kindIs "map" $d.status -}}
    {{- $status = $d.status.status -}}
    {{- $pausedUntil = $d.status.pausedUntil -}}
{{- end -}}

{{- /* freshness: prefer the detail's own updatedAt, fall back to the parent list object's */ -}}
{{- $updatedAt := $d.updatedAt -}}
{{- if not $updatedAt -}}
    {{- range $.externalData.stay_subscription -}}
        {{- if eq (.id | toString) $id -}}{{- $updatedAt = .updatedAt -}}{{- end -}}
    {{- end -}}
{{- end -}}

{{- /* product lines; nested objects may be absent OR explicitly null, so guard with kindIs */ -}}
{{- $lines := list -}}
{{- range $line := $d.lines -}}
    {{- $entry := dict
        "title" $line.title
        "variantTitle" $line.variantTitle
        "quantity" $line.quantity
    -}}
    {{- if ne $line.id nil -}}
        {{- $_ := set $entry "id" (kindIs "float64" $line.id | ternary ($line.id | int64 | toString) ($line.id | toString)) -}}
    {{- end -}}
    {{- if kindIs "map" $line.currentPrice -}}
        {{- $_ := set $entry "price" $line.currentPrice.amount -}}
    {{- end -}}
    {{- if kindIs "map" $line.lineDiscountedPrice -}}
        {{- $_ := set $entry "discountedPrice" $line.lineDiscountedPrice.amount -}}
    {{- end -}}
    {{- if kindIs "map" $line.variantImage -}}
        {{- $_ := set $entry "variantImageSrc" $line.variantImage.originalSrc -}}
    {{- end -}}
    {{- $lines = append $lines $entry -}}
{{- end -}}

{{- $lineCount := $d.lineCount -}}
{{- if not $lineCount -}}{{- $lineCount = len $lines -}}{{- end -}}

{{- $out := dict
    "id" $id
    "updatedAt" $updatedAt
    "status" $status
    "pausedUntil" $pausedUntil
    "lineCount" $lineCount
    "scheduledBillingDate" $d.scheduledBillingDate
    "lines" $lines
-}}

{{- /* emit billingPolicy and deliveryAddress only when present, so absent stays null */ -}}
{{- if kindIs "map" $d.billingPolicy -}}
    {{- $_ := set $out "billingPolicy" (dict
        "interval" $d.billingPolicy.interval
        "intervalCount" $d.billingPolicy.intervalCount
    ) -}}
{{- end -}}
{{- if kindIs "map" $d.deliveryMethod -}}
    {{- if kindIs "map" $d.deliveryMethod.address -}}
        {{- $a := $d.deliveryMethod.address -}}
        {{- $_ := set $out "deliveryAddress" (dict
            "firstName" $a.firstName
            "lastName" $a.lastName
            "address1" $a.address1
            "address2" $a.address2
            "city" $a.city
            "province" $a.province
            "provinceCode" $a.provinceCode
            "country" $a.country
            "countryCode" $a.countryCode
            "zip" $a.zip
            "phone" $a.phone
        ) -}}
    {{- end -}}
{{- end -}}

{{- $out | toJson -}}
