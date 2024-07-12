{{- if ne .integration.secrets.credentials nil}}
    {{- $username := ""}}
    {{- if ne .integration.secrets.credentials.username nil}}{{$username = .integration.secrets.credentials.username}}{{end}}

    {{- $password := ""}}
    {{- if ne .integration.secrets.credentials.password nil}}{{$password = .integration.secrets.credentials.password}}{{end}}

    {{- if or (gt (len $username) 0) (gt (len $password) 0) -}}
        Basic {{b64enc (printf "%s:%s" $username $password)}}
    {{- end}}
{{- end}}