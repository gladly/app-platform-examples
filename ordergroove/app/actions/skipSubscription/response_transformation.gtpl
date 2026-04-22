{{- if and .response (ne .response.statusCode 200) -}}
  {{- if eq .response.statusCode 404 -}}
    {{ stop (printf "order with id %s does not exist" .inputs.orderId) }}
  {{- end -}}
  {{- if eq .response.statusCode 400 -}}
    {{- if and .rawData.subscription -}}
      {{- if gt (len .rawData.subscription) 0 -}}
      {{- $messages := "" -}}
      {{- range $i, $msg := .rawData.subscription -}}
        {{- if $i -}}, {{- end -}}
        {{- $messages = printf "%s%s" $messages $msg -}}
      {{- end -}}
      {{ stop (printf "skip subscription failed: %s" $messages) }}
      {{- end -}}
    {{- end -}}
  {{- end -}}
  {{ fail (printf "skip subscription failed: %d %s" .response.statusCode .response.body) }}
{{- end -}}
{{- $data := dict -}}
{{- range $key, $value := .rawData -}}
  {{- if eq $key "created" -}}
    {{- if $value -}}
      {{- $data = set $data $key (date "2006-01-02T15:04:05Z" (toDate "2006-01-02 15:04:05" $value)) -}}
    {{- else -}}
      {{- $data = set $data $key nil -}}
    {{- end -}}
  {{- else if eq $key "place" -}}
    {{- if $value -}}
      {{- $data = set $data $key (date "2006-01-02T15:04:05Z" (toDate "2006-01-02 15:04:05" $value)) -}}
    {{- else -}}
      {{- $data = set $data $key nil -}}
    {{- end -}}
  {{- else if eq $key "cancelled" -}}
    {{- if and $value (ne $value "null") -}}
      {{- $data = set $data $key (date "2006-01-02T15:04:05Z" (toDate "2006-01-02 15:04:05" $value)) -}}
    {{- else -}}
      {{- $data = set $data $key nil -}}
    {{- end -}}
  {{- else if eq $key "status" -}}
    {{- /* Map numeric status to OrderStatus enum string */ -}}
    {{- $status := int $value -}}
    {{- if eq $status 1 -}}
      {{- $data = set $data $key "UNSENT" -}}
    {{- else if eq $status 3 -}}
      {{- $data = set $data $key "REJECTED" -}}
    {{- else if eq $status 5 -}}
      {{- $data = set $data $key "SUCCESS" -}}
    {{- else if eq $status 6 -}}
      {{- $data = set $data $key "SEND_NOW" -}}
    {{- else if eq $status 11 -}}
      {{- $data = set $data $key "PENDING_PLACEMENT" -}}
    {{- else if eq $status 15 -}}
      {{- $data = set $data $key "GENERIC_ERROR_RESPONSE" -}}
    {{- else if eq $status 18 -}}
      {{- $data = set $data $key "CREDIT_CARD_RETRY" -}}
    {{- else -}}
      {{- $data = set $data $key nil -}}
    {{- end -}}
  {{- else -}}
    {{- $data = set $data $key $value -}}
  {{- end -}}
{{- end -}}
{{toJson $data}}
