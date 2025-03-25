package com.makibeans.util;

import com.makibeans.dto.ProductPageDTO;
import com.makibeans.dto.ProductResponseDTO;
import com.makibeans.mapper.ProductMapper;
import com.makibeans.model.Product;
import com.makibeans.model.ProductVariant;
import org.springframework.context.annotation.Bean;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProductFilter {
    private final Map<String, String> filters;
    private final List<Product> products;

    public ProductFilter(Map<String, String> filters, List<Product> products) {
        this.filters = filters;
        this.products = products;
    }

    public ProductPageDTO filterAndPaginate(ProductMapper productMapper)  {

        //extract multi-value filters
        List<Long> categoryIdValues = FilterUtils.extractLongList(filters, "categoryId");
        List<String> categoryNameValues = FilterUtils.extractStringList(filters, "categoryName");
        List<Long> sizeIdValues = FilterUtils.extractLongList(filters, "sizeId");
        List<String> sizeNameValues = FilterUtils.extractStringList(filters, "sizeName");
        List<String> skuValues = FilterUtils.extractStringList(filters, "sku");

        //extract single-value filters
        Long minPrice = FilterUtils.extractLong(filters, "minPrice").orElse(null);
        Long maxPrice = FilterUtils.extractLong(filters, "maxPrice").orElse(null);
        Long stock = FilterUtils.extractLong(filters, "stock").orElse(null);

        //extract query
        String query = FilterUtils.extractLowerCase(filters, "query").orElse(null);

        //extract sort
        String sort = FilterUtils.extractLowerCase(filters, "sort").orElse(null);

        // default to ascending
        String order = FilterUtils.extractLowerCase(filters, "order").orElse("asc");

        //extract pagination
        int page = FilterUtils.extractInt(filters, "page").orElse(0); // default page is 0
        int size = FilterUtils.extractInt(filters, "size").orElse(12); // default size is 12

        //list the know params
        Set<String> knownParams = Set.of("categoryId", "categoryName", "minPrice", "maxPrice", "sizeId", "sizeName", "sku", "stock", "query", "sort", "order", "page", "size");

        //extracts the unknown params i.e. the attribute filters
        Map<String, String> attributeFilters = filters.entrySet().stream().
                filter(f -> !knownParams.contains(f.getKey())).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        //stream products
        List<Product> allProducts = products;
        Stream<Product> products = allProducts.stream();

        //filter by categoryId
        if (!categoryIdValues.isEmpty()) {
            products = products.filter(p -> categoryIdValues.contains(p.getCategory().getId()));
        }

        //filter by categoryName
        if (!categoryNameValues.isEmpty()) {
            products = products.filter(p -> categoryNameValues.contains(p.getCategory().getName().toLowerCase()));
        }

        //filter by minPrice
        if (minPrice != null) {
            products = products.filter(p -> p.getProductVariants().stream().anyMatch(v -> v.getPriceInCents() >= minPrice));
        }

        //filter by maxPrice
        if (maxPrice != null) {
            products = products.filter(p -> p.getProductVariants().stream().anyMatch(v -> v.getPriceInCents() <= maxPrice));
        }

        //filter by size id
        if (!sizeIdValues.isEmpty()) {
            products = products.filter(p -> p.getProductVariants().stream().anyMatch(v -> sizeIdValues.contains(v.getSize().getId())));
        }

        //filter by size name
        if (!sizeNameValues.isEmpty()) {
            products = products.filter(p -> p.getProductVariants().stream().anyMatch(v -> sizeNameValues.contains(v.getSize().getName().toLowerCase())));
        }

        // filter by SKU
        if (!skuValues.isEmpty()) {
            products = products.filter(p -> p.getProductVariants().stream().anyMatch(v -> skuValues.contains(v.getSku().toLowerCase())));
        }

        // filter by stock
        if (stock != null) {
            products = products.filter(p -> p.getProductVariants().stream().anyMatch(v -> v.getStock() >= stock));
        }

        // filter by product attributes
        products = products.filter(product ->

                // for each product, check all attribute filters (e.g. origin=chili,argentina)
                attributeFilters.entrySet().stream().allMatch(attributeFilter -> {

                    // get list of cleaned attribute filter values, e.g. origin=chili,argentina => ["chili", "argentina"]
                    List<String> values = FilterUtils.splitAndNormalize(attributeFilter.getValue());

                    // match product attributes: template name matches filter key AND at least one value matches
                    return product.getProductAttributes().stream().anyMatch(productAttribute ->
                            productAttribute.getAttributeTemplate().getName().equalsIgnoreCase(attributeFilter.getKey()) &&
                                    productAttribute.getAttributeValues().stream().anyMatch(attributeValue ->
                                            values.contains(attributeValue.getValue().toLowerCase()) // normalize comparison
                                    )
                    );
                })
        );

        //filter by search query on product name, description, attribute values, and attribute template names
        if (query != null && !query.isBlank()) {
            String lowerQuery = query.toLowerCase();
            products = products.filter(p ->
                    //search product name
                    p.getProductName().toLowerCase().contains(lowerQuery) ||

                            //search product description
                            p.getProductDescription().toLowerCase().contains(lowerQuery) ||

                            //search product attribute values
                            p.getProductAttributes().stream().anyMatch(pa ->
                                    pa.getAttributeValues().stream().anyMatch(v ->
                                            v.getValue().toLowerCase().contains(lowerQuery))) ||

                            //search attribute template names
                            p.getProductAttributes().stream().anyMatch(pa ->
                                    pa.getAttributeTemplate().getName().toLowerCase().contains(lowerQuery))
            );
        }

        //define the sort comparator
        if (sort != null) {

            Comparator<Product> comparator;

            switch (sort) {

                case "categoryName" -> comparator = Comparator
                        .comparing(product -> product.getCategory().getName(), String.CASE_INSENSITIVE_ORDER);
                case "priceInCents" -> comparator = Comparator
                        .comparing(product -> product.getProductVariants()
                                .stream().mapToLong(ProductVariant::getPriceInCents)
                                .min()
                                .orElse(Integer.MAX_VALUE));
                case "productName" -> comparator = Comparator
                        .comparing(Product::getProductName, String.CASE_INSENSITIVE_ORDER);
                case "sizeName" -> comparator = Comparator
                        .comparing(product -> product.getProductVariants().stream()
                                .map(v -> v.getSize().getName())
                                .min(String.CASE_INSENSITIVE_ORDER).orElse(""));
                default -> comparator = Comparator
                        .comparing(product -> product.getProductVariants()
                                .stream().mapToLong(ProductVariant::getPriceInCents)
                                .min()
                                .orElse(Integer.MAX_VALUE)); // default to priceInCents
            }

            //sort by comparator
            if (comparator != null) {
                comparator = order.equals("desc") ? comparator.reversed() : comparator;
                products = products.sorted(comparator);
            }

        }

        //filtered products
        List<Product> filtered = products.toList();

        //pagination
        Long totalElements = (long) filtered.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        List<ProductResponseDTO> pageContent = filtered.stream()
                .skip((long) page * size)
                .limit(size)
                .map(productMapper::toResponseDTO)
                .toList();

        //return paginated content
        return new ProductPageDTO(pageContent, page, size, totalElements, totalPages);
    }
}
