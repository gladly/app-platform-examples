{{- $customerId := (first .externalData.recharge_customer).id -}} 

{{- printf "https://api.rechargeapps.com/charges?customer_id=%s" $customerId -}}