{{- $customerId := (first .externalData.recharge_customer).id -}} 

{{- printf "https://api.rechargeapps.com/subscriptions?customer_id=%s" $customerId -}}