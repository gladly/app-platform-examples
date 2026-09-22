{{- /* quantity arrives as free text; coerce to an int for the Int! action input. */ -}}
{
  "chargeId": {{ toJson .chargeId }},
  "externalVariantId": {{ toJson .externalVariantId }},
  "quantity": {{ .quantity | int }}
}
