{{- $sel := fromJson .chargeSelection -}}
{
  "operation": {{ toJson .operation }},
  "chargeId": {{ toJson $sel.chargeId }},
  "subscriptionId": {{ toJson $sel.subscriptionId }}
}
