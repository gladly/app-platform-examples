{{- /* Form attrs are always strings on the wire, so Int/Float inputs convert here, guarded
       by a regex on the RAW string: sprig's `int` turns "abc" into 0 silently. */ -}}
{{- $out := dict -}}
{{- $vsubscriptionId := .subscriptionId | default "" | toString | trim -}}
{{- if ne $vsubscriptionId "" -}}{{- $out = set $out "subscriptionId" $vsubscriptionId -}}{{- end -}}
{{- /* "__none__" is the form's "no saved address" option (see form.gtpl). It means the agent
       backed out of the picker, so it is dropped exactly like a blank and the typed fields apply. */ -}}
{{- $vnewShippingAddressPlatformId := .newShippingAddressPlatformId | default "" | toString | trim -}}
{{- if and (ne $vnewShippingAddressPlatformId "") (ne $vnewShippingAddressPlatformId "__none__") -}}{{- $out = set $out "newShippingAddressPlatformId" $vnewShippingAddressPlatformId -}}{{- end -}}
{{- $vfirstName := .firstName | default "" | toString | trim -}}
{{- if ne $vfirstName "" -}}{{- $out = set $out "firstName" $vfirstName -}}{{- end -}}
{{- $vlastName := .lastName | default "" | toString | trim -}}
{{- if ne $vlastName "" -}}{{- $out = set $out "lastName" $vlastName -}}{{- end -}}
{{- $vcompany := .company | default "" | toString | trim -}}
{{- if ne $vcompany "" -}}{{- $out = set $out "company" $vcompany -}}{{- end -}}
{{- $vaddress1 := .address1 | default "" | toString | trim -}}
{{- if ne $vaddress1 "" -}}{{- $out = set $out "address1" $vaddress1 -}}{{- end -}}
{{- $vaddress2 := .address2 | default "" | toString | trim -}}
{{- if ne $vaddress2 "" -}}{{- $out = set $out "address2" $vaddress2 -}}{{- end -}}
{{- $vcity := .city | default "" | toString | trim -}}
{{- if ne $vcity "" -}}{{- $out = set $out "city" $vcity -}}{{- end -}}
{{- $vprovince := .province | default "" | toString | trim -}}
{{- if ne $vprovince "" -}}{{- $out = set $out "province" $vprovince -}}{{- end -}}
{{- $vcountry := .country | default "" | toString | trim -}}
{{- if ne $vcountry "" -}}{{- $out = set $out "country" $vcountry -}}{{- end -}}
{{- $vzip := .zip | default "" | toString | trim -}}
{{- if ne $vzip "" -}}{{- $out = set $out "zip" $vzip -}}{{- end -}}
{{- $vdoorCode := .doorCode | default "" | toString | trim -}}
{{- if ne $vdoorCode "" -}}{{- $out = set $out "doorCode" $vdoorCode -}}{{- end -}}
{{- $vphone := .phone | default "" | toString | trim -}}
{{- if ne $vphone "" -}}{{- $out = set $out "phone" $vphone -}}{{- end -}}
{{ toJson $out }}
