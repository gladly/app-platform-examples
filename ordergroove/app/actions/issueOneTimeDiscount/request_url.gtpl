{{- /*
     GUARDED action (house idiom, shipped on recharge adjust_credit and rivo adjust_points).

     Every gate lives HERE, in request_url.gtpl, and fires BEFORE the request body is
     built. Not in ui/forms/: Team Assist invokes actions directly and never renders a
     form, so a dropdown or a confirmation checkbox constrains only one of the two
     callers.

     This endpoint issues money. Ordergroove documents no ceiling on `value`, no
     idempotency key (a desktop retry or a repeated Team Assist call DOUBLE-ISSUES),
     and stacking_type 'additional' lets discounts stack. An admin on/off toggle alone
     caps nothing, so the cap is mandatory and unset means refuse.
*/ -}}

{{- /* 1. Merchant must switch the capability on. Default the configuration map first -
       reading .integration.configuration.X on a config with no configuration object
       blanks the card with "index of untyped nil". */ -}}
{{- $cfg := default (dict) .integration.configuration -}}
{{- if ne (printf "%v" (get $cfg "enableIssueDiscount")) "true" -}}
  {{ stop "Issuing discounts is turned off for this store. This is a Gladly setting, not an Ordergroove one: a Gladly admin turns it on under Settings, App Platform, Ordergroove, by setting \"Enable one-time discounts\" to Enabled." }}
{{- end -}}

{{- /* 2. Explicit confirmation. The checkbox defaults to off, so an omitted or false
       value refuses - the same idiom as rise `issueStoreCredit`, the repo's other
       capped money action. */ -}}
{{- if ne (.inputs.confirmed | default false) true -}}
  {{ stop "To issue a discount you must tick the confirmation checkbox." }}
{{- end -}}

{{- /* 3. Exactly one target. Ordergroove: item and order are "mutually exclusive, but
       one is required". */ -}}
{{- $itemId := printf "%v" (default "" .inputs.itemId) -}}
{{- $orderId := printf "%v" (default "" .inputs.orderId) -}}
{{- if and (eq $itemId "") (eq $orderId "") -}}
  {{ stop "Choose what the discount applies to: either a single item or a whole order." }}
{{- end -}}
{{- if and (ne $itemId "") (ne $orderId "") -}}
  {{ stop "A discount applies to either one item or one order, not both. Clear one of them." }}
{{- end -}}

{{- /* 4. Value must be a number. sprig's `int`/`float64` turn "abc" into 0 silently;
       on a discount field that is a typo quietly issuing nothing, or worse. */ -}}
{{- $value := printf "%v" .inputs.value -}}
{{- if not (regexMatch "^[0-9]+(\\.[0-9]{1,2})?$" $value) -}}
  {{ stop (printf "Discount value must be a positive number such as 5 or 7.50 (got %q)." $value) }}
{{- end -}}
{{- if le (float64 $value) 0.0 -}}
  {{ stop "Discount value must be greater than zero." }}
{{- end -}}

{{- /* 5. The cap. FAIL-CLOSED: no configured cap means refuse, never unlimited. */ -}}
{{- $discountType := printf "%v" .inputs.discountType -}}
{{- if eq $discountType "percent" -}}
  {{- $capRaw := printf "%v" (default "" (get $cfg "maxDiscountPercent")) -}}
  {{- if not (regexMatch "^[0-9]+(\\.[0-9]{1,2})?$" $capRaw) -}}
    {{ stop "No maximum discount percentage is configured for this store, so percentage discounts cannot be issued. This is a Gladly setting, not an Ordergroove one: a Gladly admin sets \"Maximum discount percentage\" under Settings, App Platform, Ordergroove." }}
  {{- end -}}
  {{- if gt (float64 $value) (float64 $capRaw) -}}
    {{ stop (printf "That discount is %s%%, above this store's maximum of %s%%. Ask an admin to raise the limit or issue a smaller discount." $value $capRaw) }}
  {{- end -}}
{{- else if eq $discountType "amount" -}}
  {{- $capRaw := printf "%v" (default "" (get $cfg "maxDiscountAmount")) -}}
  {{- if not (regexMatch "^[0-9]+(\\.[0-9]{1,2})?$" $capRaw) -}}
    {{ stop "No maximum discount amount is configured for this store, so fixed-amount discounts cannot be issued. This is a Gladly setting, not an Ordergroove one: a Gladly admin sets \"Maximum discount amount\" under Settings, App Platform, Ordergroove." }}
  {{- end -}}
  {{- if gt (float64 $value) (float64 $capRaw) -}}
    {{ stop (printf "That discount is %s, above this store's maximum of %s. Ask an admin to raise the limit or issue a smaller discount." $value $capRaw) }}
  {{- end -}}
{{- else -}}
  {{ stop (printf "Discount type must be percent or amount (got %q)." $discountType) }}
{{- end -}}
https://restapi.ordergroove.com/one_time_incentives/create/
