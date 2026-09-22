{{- /* A form input is a string on the wire and Team Assist may send either a string or
       a float64. sprig's `int` turns "abc" into 0 SILENTLY, which on Quantity would
       quietly set the subscription quantity to zero. Regex the RAW string and stop loudly instead.
       This guard lives in the action, not the form: Team Assist invokes actions
       directly and never renders ui/forms/. */ -}}
{{- $quantity := printf "%v" .inputs.quantity -}}
{{- if not (regexMatch "^[0-9]+$" $quantity) -}}
  {{ stop (printf "Quantity must be a whole number greater than zero (got %q)." $quantity) }}
{{- end -}}
{{- if lt (int $quantity) 1 -}}
  {{ stop "Quantity must be greater than zero." }}
{{- end -}}
{
  "quantity": {{int $quantity}}
}
