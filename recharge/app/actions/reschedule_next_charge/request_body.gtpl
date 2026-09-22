{{- $date := .inputs.resumeDate -}}

{
    "date": {{ $date | toJson }}
}
