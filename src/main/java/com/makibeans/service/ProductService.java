package com.makibeans.service;

import com.makibeans.dto.ProductPageDTO;
import com.makibeans.dto.ProductRequestDTO;
import com.makibeans.dto.ProductResponseDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.mapper.ProductMapper;
import com.makibeans.model.Category;
import com.makibeans.model.Product;
import com.makibeans.model.ProductVariant;
import com.makibeans.repository.ProductRepository;
import com.makibeans.util.FilterUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service class for managing Products.
 * Provides methods to retrieve, create, update, and delete Products.
 */

@Service
public class ProductService extends AbstractCrudService<Product, Long> {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;

    @Autowired
    public ProductService(
            JpaRepository<Product, Long> repository,
            ProductRepository productRepository,
            CategoryService categoryService,
            ProductMapper productMapper) {
        super(repository);
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.productMapper = productMapper;
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param productId the ID of the product to retrieve.
     * @return the ProductResponseDTO representing the product.
     */

    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long productId) {
        Product product = findById(productId);
        return productMapper.toResponseDTO(product);
    }

    /**
     * Retrieves all products.
     *
     * @return a list of ProductResponseDTO representing all products.
     */

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponseDTO)
                .toList();
    }

    /**
     * Filters products based on various criteria provided in the filters map.
     * The filters can include category ID, category name, price range, size, SKU, stock, and custom attributes.
     * Uses FilterUtils to extract filter criteria.
     *
     * @param filters a map containing the filter criteria as key-value pairs.
     *                Supported keys: "categoryId", "categoryName", "minPrice", "maxPrice", "sizeId", "sizeName", "sku", "stock", "query", "sort", "order", "page", "size".
     *                Any other keys will be treated as custom attribute filters.
     * @return a ProductPageDTO representing the filtered products.
     */
    @Transactional
    public ProductPageDTO filterProducts(Map<String, String> filters) {

        //extract filters
        Long categoryId = FilterUtils.extractLong(filters, "categoryId").orElse(null);
        String categoryName = FilterUtils.extractLowerCase(filters, "categoryName").orElse(null);
        Long minPrice = FilterUtils.extractLong(filters, "minPrice").orElse(null);
        Long maxPrice = FilterUtils.extractLong(filters, "maxPrice").orElse(null);
        Long sizeId = FilterUtils.extractLong(filters, "sizeId").orElse(null);
        String sizeName = FilterUtils.extractLowerCase(filters, "sizeName").orElse(null);
        String sku = FilterUtils.extractLowerCase(filters, "sku").orElse(null);
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
        List<Product> allProducts = findAll();
        Stream<Product> products = allProducts.stream();

        //filter by categoryId
        if (categoryId != null) {
            products = products.filter(p -> p.getCategory().getId().equals(categoryId));
        }

        //filter by categoryName
        if (categoryName != null) {
            products = products.filter(p -> p.getCategory().getName().equalsIgnoreCase(categoryName));
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
        if (sizeId != null) {
            products = products.filter(p -> p.getProductVariants().stream().anyMatch(v -> v.getSize().getId().equals(sizeId)));
        }

        //filter by size name
        if (sizeName != null) {
            products = products.filter(p -> p.getProductVariants().stream().anyMatch(v -> v.getSize().getName().equalsIgnoreCase(sizeName)));
        }

        // filter by SKU
        if (sku != null) {
            products = products.filter(p -> p.getProductVariants().stream().anyMatch(v -> v.getSku().equalsIgnoreCase(sku)));
        }

        // filter by stock
        if (stock != null) {
            products = products.filter(p -> p.getProductVariants().stream().anyMatch(v -> v.getStock() >= stock));
        }

        //filter by product attributes
        products = products.filter(product ->

                //for each product loop through the product attribute filters: for each filter e.g. (origin: chili)
                attributeFilters.entrySet().stream().allMatch(attributeFilter -> //all filters should return true

                        //get all productAttributes and for each productAttribute check if attributeTemplate name attributeValues match
                        product.getProductAttributes().stream().anyMatch(productAttribute ->
                                productAttribute.getAttributeTemplate().getName().equalsIgnoreCase(attributeFilter.getKey()) &&
                                        productAttribute.getAttributeValues().stream().anyMatch(attributeValue ->
                                                attributeValue.getValue().equalsIgnoreCase(attributeFilter.getValue())
                                        )
                        )
                )
        );

        //filter by search query on product name and description
        if (query != null && !query.isBlank()) {
            String lowerQuery = query.toLowerCase();
            products = products.filter(p ->
                    p.getProductName().toLowerCase().contains(lowerQuery) ||
                            p.getProductDescription().toLowerCase().contains(lowerQuery)
            );
        }

        //define the sort comparator
        if (sort != null) {

            Comparator<Product> comparator = null;

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
        List<ProductResponseDTO> pageContent = filtered
                .stream()
                .skip((long) page * size) // if page is 2 and size is 12 skip first 12 elements
                .limit(size) //if size is 12 returns 12 elements*/
                .map(productMapper::toResponseDTO).toList();

        Long totalElements = (long) filtered.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        //return paginated content
        return new ProductPageDTO(pageContent, page, size, totalElements, totalPages);
    }

    /**
     * Creates a new product.
     *
     * @param dto the DTO containing product details.
     * @return the saved ProductResponseDTO.
     * @throws DuplicateResourceException if a product with the given name already exists.
     */


    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {

        if (productRepository.existsByProductName(dto.getProductName())) {
            throw new DuplicateResourceException("Product with name " + dto.getProductName() + " already exists.");
        }

        Category category = categoryService.findById(dto.getCategoryId());

        Product product = Product.builder()
                .productName(dto.getProductName().trim().toLowerCase())
                .productDescription(dto.getProductDescription().trim().toLowerCase())
                .productImageUrl((dto.getProductImageUrl() != null) ? dto.getProductImageUrl().trim() : null)
                .category(category)
                .build();

        Product savedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(savedProduct);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param productId the ID of the product to delete.
     */

    @Transactional
    public void deleteProduct(Long productId) {
        delete(productId);
    }

    /**
     * Updates an existing product.
     *
     * @param productId the ID of the product to update.
     * @param dto       the DTO containing updated product details.
     * @return the updated ProductResponseDTO.
     * @throws DuplicateResourceException if a product with the given name already exists.
     */

    @Transactional
    public ProductResponseDTO updateProduct(Long productId, ProductRequestDTO dto) {
        Product productToUpdate = findById(productId);

        if (!productToUpdate.getProductName().equals(dto.getProductName()) &&
                productRepository.existsByProductName(dto.getProductName())) {
            throw new DuplicateResourceException("Product with name " + dto.getProductName() + " already exists.");
        }

        Category category = categoryService.findById(dto.getCategoryId());

        productToUpdate.setProductName(dto.getProductName().trim().toLowerCase());
        productToUpdate.setProductDescription(dto.getProductDescription().trim().toLowerCase());
        productToUpdate.setCategory(category);
        productToUpdate.setProductImageUrl(
                dto.getProductImageUrl() != null ? dto.getProductImageUrl().trim() : null
        );

        Product updatedProduct = productRepository.save(productToUpdate);
        return productMapper.toResponseDTO(updatedProduct);
    }
}
