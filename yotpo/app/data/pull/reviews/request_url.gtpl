{{- $reviewLimit := (default 10 (index .integration.configuration "reviewLimit")) -}}
{{- range .externalData.customer -}}
{{- if .email -}}
https://developers.yotpo.com/v2/{{$.integration.configuration.store_id}}/reviews?access_token={{$.integration.secrets.access_token}}&customer_email={{urlquery .email}}&count={{$reviewLimit}}&include_unpublished_reviews=true&include_nested=true&include_site_reviews=true&sort=date&direction=desc
{{ end -}}
{{- end -}}
