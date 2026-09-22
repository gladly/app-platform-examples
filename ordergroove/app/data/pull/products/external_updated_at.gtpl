{{- /* Every other pull in this app declares an updated-at, so its records refresh
       whenever the vendor's timestamp moves. Products did not, which left them the one
       record type that never re-ingests once stored - while the items and orders that
       point at them (Item.productDetail / Subscription.productDetail via @childIds)
       are re-ingested continuously. Observed live 2026-09-16 on a UAT profile: items
       ingested right after an app upgrade could not resolve a product that older items
       and the subscription resolved fine in the same second (platform error "could not
       retrieve external data for child IDs"). Giving products the same lifecycle as
       every other type removes that asymmetry. Ordergroove's field is `last_update`,
       normalised to RFC 3339 by the transform; orphan stubs have none and fall back to
       now, so they are simply always refreshed. */ -}}
{{- .last_update | default ( dateInZone "2006-01-02T15:04:05Z07:00" now "UTC" ) -}}
