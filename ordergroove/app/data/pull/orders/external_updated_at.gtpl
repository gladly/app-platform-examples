{{- /* Serves two jobs, and they pull in different directions.

       FRESHNESS: the platform re-ingests a record when this value moves, so it has to
       track real changes. Ordergroove's `updated` does that.

       ORDERING: the platform also asks for orders with `criteria:{ newest: N }` and
       ranks them by THIS value, then the card renders them in the order it is given.
       Sorting the array in response_transformation.gtpl does not survive that - it is
       re-ordered here. Ranking on `updated` alone puts the wrong row first: an order
       placed for Sep 30 that was created at 14:34:18 sorts BELOW one placed Sep 16 and
       rejected at 14:34:21, so the agent sees Sep 16 above Sep 30 (observed live on a
       real profile 2026-09-16).

       So emit whichever of `place` and `updated` is later. A future-dated upcoming
       order sorts to the top on its place date, every settled order sorts on the last
       time it actually changed, and any change that pushes `updated` past `place` still
       moves the value, so freshness is preserved for exactly the records that can
       still change. Both fields are already normalised to RFC 3339 by the transform,
       which sorts correctly as text. */ -}}
{{- $place := printf "%v" (default "" .place) -}}
{{- $updated := printf "%v" (default "" .updated) -}}
{{- if and (ne $place "") (ne $updated "") -}}
{{- if gt $place $updated -}}{{- $place -}}{{- else -}}{{- $updated -}}{{- end -}}
{{- else if ne $place "" -}}
{{- $place -}}
{{- else if ne $updated "" -}}
{{- $updated -}}
{{- else -}}
{{- dateInZone "2006-01-02T15:04:05Z07:00" now "UTC" -}}
{{- end -}}
