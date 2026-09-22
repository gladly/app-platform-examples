{{- /* Unpack the packed charge selection ({chargeId, refundableAmount}) into the action's inputs and
       pass through the refund-type/amount/confirmation fields. refundableAmount rides along so the
       action can cap a full refund. */ -}}
{{- $sel := fromJson .chargeSelection -}}
{
  "chargeId": {{ toJson $sel.chargeId }},
  "refundableAmount": {{ toJson $sel.refundableAmount }},
  "fullRefund": {{ toJson (.fullRefund | default "false") }},
  "amount": {{ toJson (.amount | default "") }},
  "confirmationCopy": {{ toJson (.confirmationCopy | default "") }}
}
