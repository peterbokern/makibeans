package com.makibeans.filter;

import com.makibeans.dto.ProductPageDTO;
import com.makibeans.dto.ProductResponseDTO;
import com.makibeans.mapper.ProductMapper;
import com.makibeans.model.Product;
import com.makibeans.model.ProductVariant;
import com.makibeans.service.AttributeTemplateService;
import com.makibeans.util.FilterUtils;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class for filtering, sorting and paginating products.
 */

public class ProductFilter {
    private final Map<String, String> filters;
    private final List<Product> products;
    private final ProductMapper productMapper;
    private List<Long> categoryIdValues;
    private List<String> categoryNameValues;
    private List<Long> sizeIdValues;
    private List<String> sizeNameValues;
    private List<String> skuValues;
    private Long minPrice;
    private Long maxPrice;
    private Long stock;
    private String search;
    private String sort;
    private String order;
    private int page;
    private int size;
    private final Set<String> validAttributeKeys;

    private static final Set<String> KNOWN_PARAMS = Set.of(
            "categoryId", "categoryName", "minPrice", "maxPrice",
            "sizeId", "sizeName", "sku", "stock", "query", "sort", "order", "page", "size", "search"
    );

    @Builder
    public ProductFilter(Map<String, String> filters, List<Product> products, ProductMapper productMapper, AttributeTemplateService attributeTemplateService, Set<String> validAttributeKeys) {
        this.filters = filters;
        this.products = products;
        this.productMapper = productMapper;
        this.validAttributeKeys = validAttributeKeys;
    }

    /**
     * Filters and paginates the products based on the provided filters.
     *
     * @return a ProductPageDTO representing the filtered and paginated products.
     */

    public ProductPageDTO apply() {

        // Extract filters into instance fields
        extractFilters();

        //define validParams
        Set<String> validParams = new HashSet<>();
        validParams.addAll(KNOWN_PARAMS);
        validParams.addAll(validAttributeKeys);

        //validate
        FilterUtils.validateParams(filters, validParams);

        // Create a stream from the product list
        Stream<Product> stream = products.stream();

        // Apply all filters and sorting to the product stream
        stream = applyAllFilters(stream);
        stream = applySorting(stream);

        // Collect the filtered products into a list
        List<Product> filtered = stream.toList();

        // Apply pagination to the filtered list and return the paged result
        return applyPagination(filtered);
    }

    /**
     * Extracts filters from the provided map and assigns them to instance fields.
     * This method handles multi-value filters, single-value filters, query, sort, order, and pagination.
     */

    private void extractFilters() {
        //extract multi-value filters
        categoryIdValues = FilterUtils.extractLongList(filters, "categoryId");
        categoryNameValues = FilterUtils.extractStringList(filters, "categoryName");
        sizeIdValues = FilterUtils.extractLongList(filters, "sizeId");
        sizeNameValues = FilterUtils.extractStringList(filters, "sizeName");
        skuValues = FilterUtils.extractStringList(filters, "sku");

        //extract single-value filters
        minPrice = FilterUtils.extractLong(filters, "minPrice").orElse(null);
        maxPrice = FilterUtils.extractLong(filters, "maxPrice").orElse(null);
        stock = FilterUtils.extractLong(filters, "stock").orElse(null);

        //extract query
        search = FilterUtils.extractLowerCase(filters, "search").orElse(null);

        //extract sort
        sort = FilterUtils.extractLowerCase(filters, "sort").orElse(null);

        // default to ascending
        order = FilterUtils.extractLowerCase(filters, "order").orElse("asc");

        //extract pagination
        page = FilterUtils.extractInt(filters, "page").orElse(0); // default page is 0
        size = FilterUtils.extractInt(filters, "size").orElse(12); // default size is 12
    }

    /**
     * Applies all filters to the given stream of products.
     *
     * Instead of Stream.of() use Stream.<Type>>of(...) to explicitly define the type.
     * This is a stream of method references "Function<Stream<Product>, Stream<Product>>"
     * In the method reference, you declare the input and output type, in this case, both <Stream<Product>
     */

    private Stream<Product> applyAllFilters(Stream<Product> products) {
        return Stream.<Function<Stream<Product>, Stream<Product>>>of(
                        this::applyCategoryFilters,
                        this::applyPriceFilters,
                        this::applySizeFilters,
                        this::applySKUandStockFilters,
                        this::applyAttributeFilters,
                        this::applySearchQueryFilter
                ).reduce(Function.identity(), Function::andThen) //starts with products and then chains each method in sequence
                .apply(products);
    }

    /**
     * Applies sorting to the given stream of products based on the provided sort criteria.
     * The sorting can be done by category name, price in cents, product name, or size name.
     * If no sort criteria is provided, the default sorting is by price in cents.
     *
     * @param products the stream of products to sort.
     * @return the sorted stream of products.
     */

    private Stream<Product> applySorting(Stream<Product> products) {

        if (sort != null) {

            Comparator<Product> comparator;

            switch (sort) {

                case "categoryName" ->
                        comparator = Comparator
                        .comparing(product -> product.getCategory().getName(), String.CASE_INSENSITIVE_ORDER);
                case "priceInCents" ->
                        comparator = Comparator
                        .comparing(product -> product.getProductVariants()
                                .stream().mapToLong(ProductVariant::getPriceInCents)
                                .min()
                                .orElse(Integer.MAX_VALUE));
                case "productName" ->
                        comparator = Comparator
                        .comparing(Product::getProductName, String.CASE_INSENSITIVE_ORDER);
                case "sizeName" ->
                        comparator = Comparator
                        .comparing(product -> product.getProductVariants().stream()
                                .map(v -> v.getSize().getName())
                                .min(String.CASE_INSENSITIVE_ORDER).orElse(""));
                default ->
                        comparator = Comparator
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

        return products;
    }

    /**
     * Applies pagination to the filtered list of products.
     *
     * @param filtered the list of filtered products.
     * @return a ProductPageDTO representing the paginated products.
     */

    private ProductPageDTO applyPagination(List<Product> filtered) {
        //pagination
        Long totalElements = (long) filtered.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        List<ProductResponseDTO> pageContent = filtered.stream()
                .skip((long) page * size)
                .limit(size)
                .map(productMapper::toResponseDTO)
                .toList();

        //return paginated content
        return ProductPageDTO.builder()
                .content(pageContent)
                .page(page)
                .totalPages(totalPages)
                .size(size)
                .totalElements(totalElements)
                .build();
    }

    /**
     * Applies category filters to the given stream of products.
     *
     * @param products the stream of products to filter.
     * @return the filtered stream of products.
     */

    private Stream<Product> applyCategoryFilters(Stream<Product> products) {
        //filter by categoryId
        if (!categoryIdValues.isEmpty()) {
            products = products.filter(p -> categoryIdValues.contains(p.getCategory().getId()));
        }

        //filter by categoryName
        if (!categoryNameValues.isEmpty()) {
            products = products.filter(p -> categoryNameValues.contains(p.getCategory().getName().toLowerCase()));
        }

        return products;
    }

    /**
     * Applies price filters to the given stream of products.
     *
     * @param products the stream of products to filter.
     * @return the filtered stream of products.
     */

    private Stream<Product> applyPriceFilters(Stream<Product> products) {
        //filter by minPrice
        if (minPrice != null) {
            products = products.filter(p -> p.getProductVariants().stream().anyMatch(v -> v.getPriceInCents() >= minPrice));
        }

        //filter by maxPrice
        if (maxPrice != null) {
            products = products.filter(p -> p.getProductVariants().stream().anyMatch(v -> v.getPriceInCents() <= maxPrice));
        }

        return products;
    }

    /**
     * Applies size filters to the given stream of products.
     *
     * @param products the stream of products to filter.
     * @return the filtered stream of products.
     */

    private Stream<Product> applySizeFilters(Stream<Product> products) {

        //filter by size id
        if (!sizeIdValues.isEmpty()) {
            products = products.filter(p -> p.getProductVariants().stream().anyMatch(v -> sizeIdValues.contains(v.getSize().getId())));
        }

        //filter by size name
        if (!sizeNameValues.isEmpty()) {
            products = products.filter(p -> p.getProductVariants().stream().anyMatch(v -> sizeNameValues.contains(v.getSize().getName().toLowerCase())));
        }

        return products;
    }

    /**
     * Applies SKU and stock filters to the given stream of products.
     *
     * @param products the stream of products to filter.
     * @return the filtered stream of products.
     */

    private Stream<Product> applySKUandStockFilters(Stream<Product> products) {

        // filter by SKU
        if (!skuValues.isEmpty()) {
            products = products.filter(p -> p.getProductVariants().stream().anyMatch(v -> skuValues.contains(v.getSku().toLowerCase())));
        }

        // filter by stock
        if (stock != null) {
            products = products.filter(p -> p.getProductVariants().stream().anyMatch(v -> v.getStock() >= stock));
        }

        return products;
    }

    /**
     * Applies attribute filters to the given stream of products.
     * Extracts unknown parameters (i.e., attribute filters) from the provided filters map
     * and filters the products based on these attributes.
     *
     * @param products the stream of products to filter.
     * @return the filtered stream of products.
     */

    private Stream<Product> applyAttributeFilters(Stream<Product> products) {

        //extracts the unknown params i.e. the attribute filters
        Map<String, String> attributeFilters = filters.entrySet().stream().
                filter(f -> !KNOWN_PARAMS.contains(f.getKey())).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

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
                                    ));
                })
        );

        return products;
    }

    /**
     * Applies a search query filter to the given stream of products.
     * Filters products based on the search query, which can match the product name,
     * description, attribute values, and attribute template names.
     *
     * @param products the stream of products to filter.
     * @return the filtered stream of products.
     */

    private Stream<Product> applySearchQueryFilter(Stream<Product> products) {

        //filter by search query on product name, description, attribute values, and attribute template names
        if (search != null && !search.isBlank()) {
            String lowerQuery = search.toLowerCase();
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

        return products;
    }
}