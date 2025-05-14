{{- if and .rawData.data .rawData.data.StorefrontUsers (eq (len .rawData.data.StorefrontUsers) 0) -}}
    
    {{- stop "StorefrontUser does not exist" -}}

{{- else if and .rawData.data .rawData.data.StorefrontUsers (gt (len .rawData.data.StorefrontUsers) 1) -}}
    
    {{- stop "Skio returned more than one user for the customer profile email." -}}

{{- else -}}
    {{/* Return the first user */}}
    {{- $user := (index .rawData.data.StorefrontUsers 0) -}}
    {{- with $user -}}
    {{ toJson . -}}
    {{- end -}}
{{- end -}} 