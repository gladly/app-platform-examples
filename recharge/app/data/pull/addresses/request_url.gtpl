{{- $customerId := (first .externalData.recharge_customer).id -}} 

{{- printf "https://api.rechargeapps.com/addresses?customer_id=%s" $customerId -}}