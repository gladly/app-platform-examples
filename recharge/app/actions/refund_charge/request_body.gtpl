{{- /* Always send an explicit `amount`, never Recharge's `full_refund: true` flag. The action can't
       independently read the charge's real balance (actions get only `inputs`), so the cap in
       request_url.gtpl can only check a caller-supplied number. If a full refund sent `full_refund:
       true`, Recharge would refund the ACTUAL balance while the cap only vetted the caller's claimed
       `refundableAmount` -- an AI/API caller could pass a tiny refundableAmount to slip a large real
       refund past the cap. Sending the cap-checked number AS the amount closes that: the dollars
       vetted are exactly the dollars refunded. A full refund therefore sends refundableAmount (the
       remaining balance the form computed and request_url.gtpl already validated and capped); a
       partial sends the typed amount. fullRefund arrives as a "true"/"false" string from the form. */ -}}
{{- $isFull := eq (.inputs.fullRefund | default "" | toString | lower | trim) "true" -}}
{{- if $isFull -}}
{{ toJson (dict "amount" (.inputs.refundableAmount | toString | trim)) }}
{{- else -}}
{{ toJson (dict "amount" (.inputs.amount | toString | trim)) }}
{{- end -}}
