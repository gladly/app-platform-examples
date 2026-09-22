{{- $input := dict "email" (.inputs.email | toString | trim) -}}
{{- if and (ne .inputs.returnToPath nil) (ne (.inputs.returnToPath | toString | trim) "") -}}{{- $input = set $input "returnToPath" (.inputs.returnToPath | toString | trim) -}}{{- end -}}
{
  "query": "mutation generateMagicLink($input: GenerateMagicLinkInput!) { generateMagicLink(input: $input) { ok magicLinkUrl expiresAt error } }",
  "variables": {
    "input": {{ toJson $input }}
  }
}
