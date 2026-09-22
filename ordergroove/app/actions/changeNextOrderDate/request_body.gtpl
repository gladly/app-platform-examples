{{- /* Ordergroove expects YYYY-MM-DD and documents that the date "should be in the
       future". There is no native date picker, so the format check lives here. */ -}}
{{- $orderDate := printf "%v" .inputs.orderDate -}}
{{- if not (regexMatch "^[0-9]{4}-[0-9]{2}-[0-9]{2}$" $orderDate) -}}
  {{ stop (printf "Next order date must be in YYYY-MM-DD format (got %q)." $orderDate) }}
{{- end -}}
{
  "order_date": "{{$orderDate}}"
}
