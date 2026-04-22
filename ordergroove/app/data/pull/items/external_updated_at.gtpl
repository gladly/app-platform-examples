{{- .order_updated | default ( dateInZone "2006-01-02T15:04:05Z07:00" now "UTC" ) -}}
