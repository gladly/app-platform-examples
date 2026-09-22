{{- /* Only the supplied properties are sent so that omitted ones are left untouched. Fields are
       tested with `ne nil` rather than truthiness so that an explicit empty value clears them:
       "" for a scalar, [] for emails/phones. Gladly replaces emails and phones wholesale — a
       single entry cannot be added or removed — so the caller must pass the complete list. */ -}}
{{- $profile := dict -}}
{{- if ne .inputs.name nil -}}
  {{- $_ := set $profile "name" .inputs.name -}}
{{- end -}}
{{- if ne .inputs.image nil -}}
  {{- $_ := set $profile "image" .inputs.image -}}
{{- end -}}
{{- if ne .inputs.address nil -}}
  {{- $_ := set $profile "address" .inputs.address -}}
{{- end -}}
{{- if ne .inputs.emails nil -}}
  {{- $emails := list -}}
  {{- range .inputs.emails -}}
    {{- $email := dict -}}
    {{- range $field, $value := . -}}
      {{- if ne $value nil -}}
        {{- $_ := set $email $field $value -}}
      {{- end -}}
    {{- end -}}
    {{- $emails = append $emails $email -}}
  {{- end -}}
  {{- $_ := set $profile "emails" $emails -}}
{{- end -}}
{{- if ne .inputs.phones nil -}}
  {{- $phones := list -}}
  {{- range .inputs.phones -}}
    {{- $phone := dict -}}
    {{- range $field, $value := . -}}
      {{- if ne $value nil -}}
        {{- $_ := set $phone $field $value -}}
      {{- end -}}
    {{- end -}}
    {{- $phones = append $phones $phone -}}
  {{- end -}}
  {{- $_ := set $profile "phones" $phones -}}
{{- end -}}
{{- /* An empty patch would come back 204 and read as a successful update that changed nothing. */ -}}
{{ if eq (len $profile) 0 }}{{ stop "At least one customer profile field to update is required (name, image, address, emails or phones)." }}{{ end }}
{{- toJson $profile -}}
