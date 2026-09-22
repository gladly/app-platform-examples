{{- $input := dict "subscriptionId" (.inputs.subscriptionId | toString) -}}
{{- /* ABSENT is not EMPTY, and the two callers need them kept apart. Gladly AI omits `note`
       when it is changing nothing, so an absent note must leave the stored note alone - drop
       the key entirely. An agent who submits the form with the field cleared means "clear it",
       so an empty note must be SENT as an empty string, not dropped.

       UpdateSubscriptionNoteInput.note is nullable and Skio accepts "" as a clear - confirmed
       live 2026-09-09: an empty string wipes the note and returns ok: true. */ -}}
{{- if ne .inputs.note nil -}}
    {{- $note := .inputs.note | toString -}}
    {{- if eq (trim $note) "" -}}{{- $note = "" -}}{{- end -}}
    {{- $input = set $input "note" $note -}}
{{- end -}}
{
  "query": "mutation updateSubscriptionNote($input: UpdateSubscriptionNoteInput!) { updateSubscriptionNote(input: $input) { note ok } }",
  "variables": {
    "input": {{ toJson $input }}
  }
}
