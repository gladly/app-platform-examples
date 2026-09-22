{{- $sel := fromJson .frequencySelection -}}
{
  "subscriptionId": {{ toJson $sel.subscriptionId }},
  "orderIntervalFrequency": {{ $sel.orderIntervalFrequency | int }},
  "orderIntervalUnit": {{ toJson $sel.orderIntervalUnit }},
  "chargeIntervalFrequency": {{ $sel.chargeIntervalFrequency | int }}
}
