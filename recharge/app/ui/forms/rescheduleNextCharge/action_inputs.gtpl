{{- $sel := fromJson .rescheduleSelection -}}
{
  "subscriptionId": {{ toJson $sel.subscriptionId }},
  "resumeDate": {{ toJson $sel.resumeDate }}
}
