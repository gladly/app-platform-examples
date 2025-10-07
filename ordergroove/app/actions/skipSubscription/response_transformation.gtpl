{{- if and .response (ne .response.statusCode 200) -}}
  {{- if eq .response.statusCode 404 -}}
    {{ stop (printf "order with id %s does not exist" .inputs.orderId) }}
  {{- end -}}
  {{- if eq .response.statusCode 403 -}}
    {{- if and .rawData.detail -}}
      {{ stop (printf "skip subscription failed: %s" .rawData.detail) }}
    {{- else -}}
      {{ stop "skip subscription failed: Authentication Failed" }}
    {{- end -}}
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
  {{- else -}}
    {{- $data = set $data $key $value -}}
  {{- end -}}
{{- end -}}
{{toJson $data}}
