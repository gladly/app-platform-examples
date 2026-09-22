{{- /* All three interval fields must be sent together; Recharge 422s if any is missing,
       and charge_interval_frequency must equal order_interval_frequency. */ -}}
{{- $oif := .inputs.orderIntervalFrequency | int64 -}}
{{- $unit := .inputs.orderIntervalUnit -}}
{{- $cif := .inputs.chargeIntervalFrequency | int64 -}}

{
    "order_interval_frequency": {{ $oif }},
    "order_interval_unit": "{{ $unit }}",
    "charge_interval_frequency": {{ $cif }}
}
