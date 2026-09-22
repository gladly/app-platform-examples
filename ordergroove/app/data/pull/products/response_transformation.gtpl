{{- /* Build set of external_product_ids returned by Ordergroove */ -}}
{{- $returnedIds := dict -}}
{{- if and .rawData .rawData.results -}}
    {{- range .rawData.results -}}
        {{- $_ := set $returnedIds .external_product_id true -}}
    {{- end -}}
{{- end -}}

{{- /* Collect IDs requested via items + subscriptions */ -}}
{{- $requestedIds := list -}}
{{- if .externalData.ordergroove_item -}}
    {{- range .externalData.ordergroove_item -}}
        {{- if .product -}}
            {{- $requestedIds = append $requestedIds .product -}}
        {{- end -}}
    {{- end -}}
{{- end -}}
{{- if .externalData.ordergroove_subscription -}}
    {{- range .externalData.ordergroove_subscription -}}
        {{- if .product -}}
            {{- $requestedIds = append $requestedIds .product -}}
        {{- end -}}
    {{- end -}}
{{- end -}}

{{- /* Orphan IDs: requested but not returned. Stubs satisfy @childIds so
       resolvers don't fail when Ordergroove omits a referenced product. */ -}}
{{- $missingIds := list -}}
{{- range ($requestedIds | uniq) -}}
    {{- if not (hasKey $returnedIds .) -}}
        {{- $missingIds = append $missingIds . -}}
    {{- end -}}
{{- end -}}

{{- /*
     Every field declared on `type Product` is emitted here, plus the card-safe
     camelCase twins.

     Before 2.3.5 this template hand-wrote eight fields and dropped the other eleven
     even though the GraphQL type declared them -- so the card could not show product
     imagery (`image_url`), link to the PDP (`detail_url`), or gate an action on
     `prepaid_eligible` / `product_type`, both of which decide whether a mutation is
     even legal on that row.

     It also interpolated values directly into JSON (`"name": "{{$product.name}}"`,
     `"live": {{$product.live}}`). A nil there renders the literal `<no value>` --
     quoted that is wrong data, unquoted it is INVALID JSON. Building a dict and
     serialising with toJson emits a proper null instead.
*/ -}}
{{- $out := list -}}
{{- if and .rawData .rawData.results -}}
    {{- range $product := .rawData.results -}}
        {{- $p := dict
              "id"                  $product.external_product_id
              "external_product_id" $product.external_product_id
              "externalProductId"   $product.external_product_id
              "merchant"            $product.merchant
              "name"                $product.name
              "price"               $product.price
              "sku"                 $product.sku
              "live"                $product.live
              "image_url"           $product.image_url
              "imageUrl"            $product.image_url
              "detail_url"          $product.detail_url
              "detailUrl"           $product.detail_url
              "product_type"        $product.product_type
              "productType"         $product.product_type
              "prepaid_eligible"    $product.prepaid_eligible
              "prepaidEligible"     $product.prepaid_eligible
              "autoship_enabled"    $product.autoship_enabled
              "autoshipEnabled"     $product.autoship_enabled
              "autoship_by_default" $product.autoship_by_default
              "incentive_group"     $product.incentive_group
              "incentiveGroup"      $product.incentive_group
              "discontinued"        $product.discontinued -}}
        {{- /* `groups` is declared [String] on the data type, but Ordergroove returns a
               list of OBJECTS ({name, group_type}). Emitting them raw would be a type
               mismatch at the GraphQL layer, so flatten to the group names. Confirm the
               real shape on the first live pull before relying on this. */ -}}
        {{- if $product.groups -}}
            {{- $names := list -}}
            {{- range $g := $product.groups -}}
                {{- if kindIs "map" $g -}}
                    {{- if (get $g "name") -}}{{- $names = append $names (get $g "name") -}}{{- end -}}
                {{- else -}}
                    {{- $names = append $names (printf "%v" $g) -}}
                {{- end -}}
            {{- end -}}
            {{- $_ := set $p "groups" $names -}}
        {{- end -}}
        {{- /* extra_data is declared String but Ordergroove may send an object. */ -}}
        {{- if $product.extra_data -}}
            {{- if kindIs "string" $product.extra_data -}}
                {{- $_ := set $p "extra_data" $product.extra_data -}}
            {{- else -}}
                {{- $_ := set $p "extra_data" (toJson $product.extra_data) -}}
            {{- end -}}
        {{- end -}}
        {{- if $product.created -}}
            {{- $_ := set $p "created" ($product.created | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z") -}}
        {{- end -}}
        {{- if $product.last_update -}}
            {{- $iso := $product.last_update | toDate "2006-01-02 15:04:05" | date "2006-01-02T15:04:05Z" -}}
            {{- $_ := set $p "last_update" $iso -}}
            {{- $_ := set $p "lastUpdate" $iso -}}
        {{- end -}}
        {{- $out = append $out $p -}}
    {{- end -}}
{{- end -}}
{{- range $id := $missingIds -}}
    {{- $out = append $out (dict
          "id"                  $id
          "external_product_id" $id
          "externalProductId"   $id
          "discontinued"        true) -}}
{{- end -}}
{{toJson $out}}
