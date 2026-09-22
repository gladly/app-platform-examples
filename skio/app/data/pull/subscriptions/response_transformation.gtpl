{{- /* One output object per subscription.

       Always-present scalars go in first; every optional or nested value is only `set` when it
       is real, because a null nested field blanks the entire card rather than one row. jsonb
       columns are carried as JSON strings - GraphQL has no arbitrary-JSON scalar here. */ -}}
{{- /* A Skio-side failure arrives on an HTTP 200 as {"errors": [...], "data": null}. Folding
       that into the not-found branch presented it to agents as an absent customer with an empty
       card, and blamed a missing profile in the logs instead of the query that actually broke,
       so it is checked first and fails loudly with Skio's own text. `with` is the only nil-safe
       way in: `errors` is absent on every successful response, and a key the payload omits is an
       invalid reflect.Value that panics both `len` and sprig's `default`. */ -}}
{{- $skioErrors := list -}}
{{- with .rawData.errors -}}
    {{- range $e := . -}}
        {{- $m := "" -}}
        {{- if kindIs "map" $e -}}
            {{- with $e.message -}}{{- $m = . | toString -}}{{- end -}}
        {{- end -}}
        {{- if eq $m "" -}}{{- $m = toJson $e -}}{{- end -}}
        {{- $skioErrors = append $skioErrors $m -}}
    {{- end -}}
{{- end -}}
{{- if gt (len $skioErrors) 0 -}}
    {{- fail (printf "Skio returned an error for the Subscriptions query: %s" (join "; " $skioErrors)) -}}
{{- end -}}
{{- $transformed := list -}}
{{- range $sub := (default list .rawData.data.Subscriptions) -}}
    {{- $out := dict
        "id" $sub.id
        "status" (default "" $sub.status)
        "createdAt" (default "" $sub.createdAt)
        "storefrontUserId" (default "" $sub.storefrontUserId)
        "platformId" (default "" $sub.platformId)
    -}}
    {{- /* A readable twin for statusContext, built here rather than in the card.
           Skio's values are SCREAMING_SNAKE - PERMANENTLY_CANCELLED, confirmed live
           2026-09-08 after a permanent cancel. The card's expression language has `title`
           but no `replace`, so `statusContext | title` renders "Permanently_Cancelled",
           underscore and all, straight at the agent. Humanise it where sprig can. */ -}}
    {{- if and (ne $sub.statusContext nil) (ne ($sub.statusContext | toString) "") -}}
        {{- $out = set $out "statusLabel"
              ($sub.statusContext | toString | lower | replace "_" " " | title) -}}
    {{- end -}}
    {{- /* statusDisplay is what the card actually prints. Doing the merge here rather than in the
           card is not tidiness: the card renders in a ~180px column, and "Cancelled (Permanently
           Cancelled)" truncated to "Cancelled..." on the desktop 2026-09-08 - hiding the half that
           says the cancellation is IRREVERSIBLE, which is the one thing an agent must not miss.
           When the humanised context already contains the status word, the context alone is
           strictly more informative, so print only that. Otherwise keep both. */ -}}
    {{- $human := $sub.status | toString | lower | replace "_" " " | title -}}
    {{- $ctx := "" -}}
    {{- if and (ne $sub.statusContext nil) (ne ($sub.statusContext | toString) "") -}}
        {{- $ctx = $sub.statusContext | toString | lower | replace "_" " " | title -}}
    {{- end -}}
    {{- if eq $ctx "" -}}
        {{- $out = set $out "statusDisplay" $human -}}
    {{- else if contains (lower $human) (lower $ctx) -}}
        {{- $out = set $out "statusDisplay" $ctx -}}
    {{- else -}}
        {{- $out = set $out "statusDisplay" (printf "%s (%s)" $human $ctx) -}}
    {{- end -}}
    {{- /* A one-line shipping address, assembled here for the same reason statusLabel is: the
           card's expression language has no working string concatenation. Its `+` is NUMERIC, so
           `', ' + city` evaluates to NaN and reaches the agent as literal "NaN" - proven live
           2026-09-08, where the product row read "Selling Plans Ski WaxNaN". Building the string
           in the pull is the only safe way to join optional parts. */ -}}
    {{- if ne $sub.ShippingAddress nil -}}
        {{- $sa := $sub.ShippingAddress -}}
        {{- $tail := list -}}
        {{- range $f := list "city" "province" "zip" -}}
            {{- $v := get $sa $f -}}
            {{- if and (ne $v nil) (ne ($v | toString) "") -}}{{- $tail = append $tail ($v | toString) -}}{{- end -}}
        {{- end -}}
        {{- $a1 := default "" $sa.address1 | toString -}}
        {{- $line := "" -}}
        {{- if and (ne $a1 "") (gt (len $tail) 0) -}}{{- $line = printf "%s, %s" $a1 (join " " $tail) -}}
        {{- else if ne $a1 "" -}}{{- $line = $a1 -}}
        {{- else if gt (len $tail) 0 -}}{{- $line = join " " $tail -}}{{- end -}}
        {{- if ne $line "" -}}{{- $out = set $out "shipsToLine" $line -}}{{- end -}}
    {{- end -}}
    {{- /* Plain optional scalars. */ -}}
    {{- range $field := list "statusContext" "updatedAt" "nextBillingDate" "cancelledAt" "currencyCode" "note" "churnPredictedAt" "nextRenewalDate" "lastBillingAttemptAt" "streakExpiresAt" -}}
        {{- $v := get $sub $field -}}
        {{- if and (ne $v nil) (ne ($v | toString) "") -}}
            {{- $out = set $out $field ($v | toString) -}}
        {{- end -}}
    {{- end -}}
    {{- /* Numeric optionals: an explicit nil check, because 0 is meaningful here - zero cycles
           completed, a waived (0.00) shipping price, an exhausted prepaid term. */ -}}
    {{- range $field := list "cyclesCompleted" "deliveryPriceOverride" "remainingCyclesUntilRenewal" "streakCount" "maxStreakCount" -}}
        {{- $v := get $sub $field -}}
        {{- if ne $v nil -}}{{- $out = set $out $field ($v | int) -}}{{- end -}}
    {{- end -}}
    {{- range $field := list "churnScore" "deliveryPrice" -}}
        {{- $v := get $sub $field -}}
        {{- if ne $v nil -}}{{- $out = set $out $field ($v | float64) -}}{{- end -}}
    {{- end -}}
    {{- /* A readable twin for churnScore, for the same reason statusDisplay exists: the raw score
           is a float64 probability, and the card printed it verbatim as
           "0.2209806144237..." - truncated mid-digit by the ~180px column, so it read as broken
           rather than as a risk. Skio's scores are probabilities in 0-1 (0.87, 0.22, 0.058 all
           seen), so a whole-number percentage is the honest rendering. No High/Medium/Low bands:
           Skio does not publish its thresholds and we will not invent them. The raw churnScore
           stays in the output above, because Rules and Gladly AI read the number. */ -}}
    {{- if ne $sub.churnScore nil -}}
        {{- $out = set $out "churnScoreDisplay"
              (printf "%.0f%%" (mulf ($sub.churnScore | float64) 100)) -}}
    {{- end -}}
    {{- if ne $sub.isPickup nil -}}{{- $out = set $out "isPickup" $sub.isPickup -}}{{- end -}}
    {{- /* churnRiskReasons is a real [String!] on Skio, so it stays a list. */ -}}
    {{- if and (ne $sub.churnRiskReasons nil) (gt (len $sub.churnRiskReasons) 0) -}}
        {{- $reasons := list -}}
        {{- range $r := $sub.churnRiskReasons -}}{{- $reasons = append $reasons ($r | toString) -}}{{- end -}}
        {{- $out = set $out "churnRiskReasons" $reasons -}}
        {{- /* Also carried as one joined string: no shipped Gladly card renders a list of bare
               strings, so the card reads this while Rules and Gladly AI read the list. */ -}}
        {{- $out = set $out "churnRiskSummary" (join ", " $reasons) -}}
    {{- end -}}
    {{- if and (ne $sub.customAttributes nil) (ne ($sub.customAttributes | toJson) "null") -}}
        {{- $out = set $out "customAttributes" ($sub.customAttributes | toJson) -}}
    {{- end -}}
    {{- /* Nested objects, only when present. */ -}}
    {{- range $field := list "BillingPolicy" "DeliveryPolicy" "PrepaidDeliveryPolicy" "ShippingAddress" -}}
        {{- $v := get $sub $field -}}
        {{- if kindIs "map" $v -}}{{- $out = set $out $field $v -}}{{- end -}}
    {{- end -}}
    {{- /* Subscription-level discounts get the same `label` twin as the line-level ones, so the
           card has exactly one way to render a discount. See the line block below for why the
           percentage is multiplied - and rounded - here, and why a fixed-value amount is left
           OUT of the label and carried as a number plus a currency code instead. */ -}}
    {{- $currency := "" -}}
    {{- if and (ne $sub.currencyCode nil) (ne ($sub.currencyCode | toString) "") -}}
        {{- $currency = $sub.currencyCode | toString -}}
    {{- end -}}
    {{- $sdisc := list -}}
    {{- range $dc := (default list $sub.Discounts) -}}
        {{- $dm := $dc -}}
        {{- $amount := "" -}}
        {{- if ne $dc.percentage nil -}}{{- $amount = printf "%v%% off" (round (mulf $dc.percentage 100) 2) -}}
        {{- else if ne $dc.fixedValue nil -}}
            {{- $dm = set $dm "fixedValue" ($dc.fixedValue | float64) -}}
            {{- if ne $currency "" -}}{{- $dm = set $dm "currencyCode" $currency -}}
            {{- else -}}{{- $amount = printf "%v off" $dc.fixedValue -}}{{- end -}}
        {{- end -}}
        {{- /* The label is the only thing that tells one discount row from another on the card, and a
               fixed-value discount with a currency puts its amount in a CurrencyValue row instead of
               the label - so a codeless one used to set no label at all, and three of them rendered as
               three identical currency rows with nothing above them. Seen live 2026-09-09: three
               consecutive "$186.29" rows. The discount's own `title` is the fallback identifier (it is
               already selected by the query at both levels). It is tried LAST, after the amount, so a
               percentage discount - which always has an amount - renders exactly as it does today. */ -}}
        {{- $code := default "" $dc.redeemCode | toString -}}
        {{- $dtitle := "" -}}
        {{- $tv := get $dc "title" -}}
        {{- if and (ne $tv nil) (ne ($tv | toString) "") -}}{{- $dtitle = $tv | toString -}}{{- end -}}
        {{- if and (ne $code "") (ne $amount "") -}}{{- $dm = set $dm "label" (printf "%s — %s" $code $amount) -}}
        {{- else if ne $code "" -}}{{- $dm = set $dm "label" $code -}}
        {{- else if ne $amount "" -}}{{- $dm = set $dm "label" $amount -}}
        {{- else if ne $dtitle "" -}}{{- $dm = set $dm "label" $dtitle -}}{{- end -}}
        {{- $sdisc = append $sdisc $dm -}}
    {{- end -}}
    {{- if gt (len $sdisc) 0 -}}
        {{- $out = set $out "Discounts" $sdisc -}}
    {{- end -}}

    {{- /* Lines. */ -}}
    {{- $lines := list -}}
    {{- range $line := (default list $sub.SubscriptionLines) -}}
        {{- $l := dict
            "id" $line.id
            "priceWithoutDiscount" (default 0.0 $line.priceWithoutDiscount | float64)
        -}}
        {{- /* ProductVariant is the one nested object worth calling out: a line whose product was
               deleted in Shopify comes back with it null, and setting a null nested key blanks the
               WHOLE card, not one row. Same `kindIs "map"` rule as the subscription-level objects
               above; the card falls back to titleOverride when it is absent. */ -}}
        {{- if kindIs "map" $line.ProductVariant -}}
            {{- $l = set $l "ProductVariant" $line.ProductVariant -}}
        {{- end -}}
        {{- range $field := list "platformId" "sellingPlanId" "productVariantId" "titleOverride" -}}
            {{- $v := get $line $field -}}
            {{- if and (ne $v nil) (ne ($v | toString) "") -}}{{- $l = set $l $field ($v | toString) -}}{{- end -}}
        {{- end -}}
        {{- /* Explicit nil checks: a truthiness check would drop a valid 0 (exhausted prepaid). */ -}}
        {{- range $field := list "quantity" "ordersRemaining" -}}
            {{- $v := get $line $field -}}
            {{- if ne $v nil -}}{{- $l = set $l $field ($v | int) -}}{{- end -}}
        {{- end -}}
        {{- if ne $line.isPrepaid nil -}}{{- $l = set $l "isPrepaid" $line.isPrepaid -}}{{- end -}}
        {{- if and (ne $line.customAttributes nil) (ne ($line.customAttributes | toJson) "null") -}}
            {{- $l = set $l "customAttributes" ($line.customAttributes | toJson) -}}
        {{- end -}}
        {{- /* Code discounts attach to the LINE, not the subscription. Proven live 2026-09-08:
               applyDiscountCode on cd83b56d produced a Discount row with subscriptionId NULL and
               subscriptionLineId set. Subscription.Discounts is keyed on subscriptionId, so it
               stayed empty and the discount was invisible to agents - which read as the vendor
               reporting a false success. It was our read.

               percentage arrives as a FRACTION (0.1 for 10%), so the label multiplies by 100 here
               rather than in the card, where a formatting slip is untestable. The product is
               rounded before printing because %v prints a float64 in full, and in IEEE-754
               doubles 0.07 * 100 is 7.000000000000001 and 0.29 * 100 is 28.999999999999996.
               appcfg's `mulf` is exact for both (probed 2026-09-09; the discount-percentage
               dataset pins the rendered labels), so this is a guard rather than a reproduced
               defect - but an agent-facing label is not the place to bet on the arithmetic.
               `round` to 2 places keeps quarter-percent codes exact, and %v then prints a whole
               number with no trailing ".0".

               A FIXED-value discount cannot be labelled the same way: `printf "%v off"` rendered
               "186.29 off" one row above a price the card prints as "$186.29", because the price
               goes through CurrencyValue and the label did not. There is no currency formatter in
               the pull and no symbol table worth hand-rolling, so the amount is handed to the card
               as a number (fixedValue) plus the subscription's currencyCode, and CurrencyValue
               formats it - the same component, so the two rows agree. The label then carries only
               the redeem code, which must stay visible. When the subscription has no currencyCode
               there is nothing for CurrencyValue to format, so the old bare-number label is kept
               rather than guessing a symbol. */ -}}
        {{- $ldisc := list -}}
        {{- range $dc := (default list $line.Discounts) -}}
            {{- $dm := dict "id" ($dc.id | toString) -}}
            {{- range $f := list "redeemCode" "title" "type" -}}
                {{- $v := get $dc $f -}}
                {{- if and (ne $v nil) (ne ($v | toString) "") -}}{{- $dm = set $dm $f ($v | toString) -}}{{- end -}}
            {{- end -}}
            {{- range $f := list "timesUsed" "maxTimesUsed" -}}
                {{- $v := get $dc $f -}}
                {{- if ne $v nil -}}{{- $dm = set $dm $f ($v | int) -}}{{- end -}}
            {{- end -}}
            {{- $amount := "" -}}
            {{- if ne $dc.percentage nil -}}
                {{- $dm = set $dm "percentage" ($dc.percentage | float64) -}}
                {{- $amount = printf "%v%% off" (round (mulf $dc.percentage 100) 2) -}}
            {{- else if ne $dc.fixedValue nil -}}
                {{- $dm = set $dm "fixedValue" ($dc.fixedValue | float64) -}}
                {{- if ne $currency "" -}}{{- $dm = set $dm "currencyCode" $currency -}}
                {{- else -}}{{- $amount = printf "%v off" $dc.fixedValue -}}{{- end -}}
            {{- end -}}
            {{- /* Same fallback chain as the subscription-level builder above, and for the same
                   reason: with no code and no amount-in-label there was nothing to tell one
                   currency row from the next. `title` is tried last so percentage labels are
                   untouched; with neither code nor title `label` stays unset and the card's
                   `when="label is not null and label != ''"` guard renders no empty row. */ -}}
            {{- $code := default "" $dc.redeemCode | toString -}}
            {{- $dtitle := "" -}}
            {{- $tv := get $dc "title" -}}
            {{- if and (ne $tv nil) (ne ($tv | toString) "") -}}{{- $dtitle = $tv | toString -}}{{- end -}}
            {{- if and (ne $code "") (ne $amount "") -}}{{- $dm = set $dm "label" (printf "%s — %s" $code $amount) -}}
            {{- else if ne $code "" -}}{{- $dm = set $dm "label" $code -}}
            {{- else if ne $amount "" -}}{{- $dm = set $dm "label" $amount -}}
            {{- else if ne $dtitle "" -}}{{- $dm = set $dm "label" $dtitle -}}{{- end -}}
            {{- $ldisc = append $ldisc $dm -}}
        {{- end -}}
        {{- if gt (len $ldisc) 0 -}}{{- $l = set $l "Discounts" $ldisc -}}{{- end -}}
        {{- $lines = append $lines $l -}}
    {{- end -}}
    {{- $out = set $out "SubscriptionLines" $lines -}}

    {{- $transformed = append $transformed $out -}}
{{- end -}}
{{- toJson $transformed -}}
