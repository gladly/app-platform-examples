{{ if (ne .response.statusCode 200) }}
    {{- $error := toJson .rawData.errors -}}
	{{- print $error | fail -}}
{{- end -}}

{{- $errors := .rawData.errors -}}

{{- /* The .gtpl dialect rejects a bare `nil` in assignment position
       (`$x := nil`); `nil` is only valid as a function argument. This helper
       carries a real null value for the untracked-quantity fields. */ -}}
{{- $null := ternary nil nil true -}}

{{- $rawVariants := list -}}
{{- $hasMore := false -}}
{{- $notFoundIndices := list -}}

{{- if ne .rawData.data nil -}}
{{- if ne .rawData.data.nodes nil -}}
{{- range $i, $node := .rawData.data.nodes -}}
    {{- if eq $node nil -}}
        {{- $notFoundIndices = append $notFoundIndices $i -}}
    {{- else if eq $node.__typename "Product" -}}
        {{- $productRef := dict "id" $node.id "title" $node.title -}}
        {{- if ne $node.variants nil -}}
            {{- if ne $node.variants.nodes nil -}}
            {{- range $node.variants.nodes -}}
                {{- $rawVariants = append $rawVariants (dict "v" . "product" $productRef) -}}
            {{- end -}}
            {{- end -}}
            {{- if ne $node.variants.pageInfo nil -}}
                {{- if $node.variants.pageInfo.hasNextPage -}}
                    {{- $hasMore = true -}}
                {{- end -}}
            {{- end -}}
        {{- end -}}
    {{- else if eq $node.__typename "ProductVariant" -}}
        {{- $productRef := $null -}}
        {{- if ne $node.product nil -}}
            {{- $productRef = dict "id" $node.product.id "title" $node.product.title -}}
        {{- end -}}
        {{- $rawVariants = append $rawVariants (dict "v" $node "product" $productRef) -}}
    {{- else -}}
        {{- $notFoundIndices = append $notFoundIndices $i -}}
    {{- end -}}
{{- end -}}
{{- end -}}
{{- end -}}

{{- /* Flatten each variant, deduped by id (a product GID plus one of its own
       variant GIDs in the same call must not list the variant twice). */ -}}
{{- $variants := list -}}
{{- $seen := dict -}}
{{- range $rawVariants -}}
    {{- $v := .v -}}
    {{- if not (hasKey $seen $v.id) -}}
        {{- $seen = set $seen $v.id true -}}

        {{- $tracked := false -}}
        {{- if ne $v.inventoryItem nil -}}
            {{- if ne $v.inventoryItem.tracked nil -}}
                {{- $tracked = $v.inventoryItem.tracked -}}
            {{- end -}}
        {{- end -}}

        {{- /* Quantity fields are meaningless for untracked variants:
               Shopify returns 0, which would mislead, so null them. */ -}}
        {{- $invQty := ternary $v.inventoryQuantity nil $tracked -}}
        {{- $sellableOnline := ternary $v.sellableOnlineQuantity nil $tracked -}}

        {{- $variants = append $variants (dict
            "id" $v.id
            "title" $v.title
            "sku" $v.sku
            "availableForSale" $v.availableForSale
            "inventoryPolicy" $v.inventoryPolicy
            "inventoryQuantity" $invQty
            "sellableOnlineQuantity" $sellableOnline
            "tracked" $tracked
            "product" .product) -}}
    {{- end -}}
{{- end -}}

{{- $errorList := list -}}
{{- if ne $errors nil -}}
    {{- range $errors -}}
        {{- $code := $null -}}
        {{- if ne .extensions nil -}}
            {{- $code = .extensions.code -}}
        {{- end -}}
        {{- $errorList = append $errorList (dict "message" .message "code" $code) -}}
    {{- end -}}
{{- end -}}
{{- range $notFoundIndices -}}
    {{- $missingId := index $.inputs.ids . -}}
    {{- $errorList = append $errorList (dict "message" (printf "Product or variant not found: %s" $missingId) "code" "not_found") -}}
{{- end -}}

{{- $output := dict
    "variants" (ternary $variants nil (gt (len $variants) 0))
    "hasMoreVariants" $hasMore
    "errors" (ternary $errorList nil (gt (len $errorList) 0)) -}}
{{- toJson $output -}}
