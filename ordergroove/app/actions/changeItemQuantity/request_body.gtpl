{{- /* sprig's `int` turns "abc" into 0 SILENTLY. Guard the RAW string in the
       ACTION, not the form - Team Assist never renders ui/forms/. */ -}}
{{- $quantity := printf "%v" .inputs.quantity -}}
{{- if not (regexMatch "^[0-9]+$" $quantity) -}}
  {{ stop (printf "Item quantity must be a whole number greater than zero (got %q)." $quantity) }}
{{- end -}}
{{- if lt (int $quantity) 1 -}}
  {{ stop "Item quantity must be greater than zero. To remove the item entirely, use Remove item instead." }}
{{- end -}}
{
  "quantity": {{int $quantity}}
}
