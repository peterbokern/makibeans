package com.makibeans.service;

import com.makibeans.dto.ProductAttributeRequestDTO;
import com.makibeans.dto.ProductAttributeResponseDTO;
import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.exeptions.ResourceNotFoundException;
import com.makibeans.mapper.ProductAttributeMapper;
import com.makibeans.model.*;
import com.makibeans.repository.ProductAttributeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductAttributeService extends AbstractCrudService<ProductAttribute, Long>{

    private final ProductAttributeRepository productAttributeRepository;
    private final ProductService productService;
    private final AttributeTemplateService attributeTemplateService;
    private final ProductAttributeMapper productAttributeMapper;
    private final AttributeValueService attributeValueService;

    @Autowired
    public ProductAttributeService(JpaRepository<ProductAttribute, Long> repository, ProductAttributeRepository productAttributeRepository, ProductService productService, AttributeTemplateService attributeTemplateService, ProductAttributeMapper productAttributeMapper, AttributeValueService attributeValueService) {
        super(repository);
        this.productAttributeRepository = productAttributeRepository;
        this.productService = productService;
        this.attributeTemplateService = attributeTemplateService;
        this.productAttributeMapper = productAttributeMapper;
        this.attributeValueService = attributeValueService;
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param id the ID of the productAttribute to retrieve.
     * @return the CategoryResponseDTO representing the category.
     * @throws IllegalArgumentException if the id is null.
     * @throws ResourceNotFoundException if the product attribute does not exist.
     */

    @Transactional
    public ProductAttributeResponseDTO getProductAttributeById(Long id) {
        ProductAttribute productAttribute = findById(id);
        System.out.println("PRINT" + productAttribute.getAttributeValues());
        return productAttributeMapper.toResponseDTO(productAttribute);
    }

    /**
     * Retrieves all product attributes.
     *
     * @return a list of productAttributeResponseDTO's representing all product attributes..
     */

    @Transactional
    public List<ProductAttributeResponseDTO> getAllProductAttributes(){
        return productAttributeRepository.findAll().stream().map(productAttributeMapper:: toResponseDTO).toList();
    }

    /**
     * Creates a product attribute.
     *
     * @param requestDTO  the dto representing the product attribute
     * @return the newly created product attribute
     * @throws IllegalArgumentException   if productId or templateId are null
     * @throws DuplicateResourceException if a product attribute already exists
     */

    @Transactional
    public ProductAttributeResponseDTO createProductAttribute(ProductAttributeRequestDTO requestDTO) {

        Long productId = requestDTO.getProductId();
        Long templateId = requestDTO.getTemplateId();

        Product product = productService.findById(productId);
        AttributeTemplate attributeTemplate = attributeTemplateService.findById(templateId);

        if (productAttributeRepository.existsByProductIdAndAttributeTemplateId(productId, templateId)) {
            throw new DuplicateResourceException("Product Attribute with product id " + productId + " and template id " + templateId + " already exists.");
        }

        ProductAttribute productAttribute = new ProductAttribute(attributeTemplate, product);
        ProductAttribute savedProductAttribute = create(productAttribute);

        return productAttributeMapper.toResponseDTO(savedProductAttribute);
    }

    /**
     * Deletes an ProductAttribute  by ID.
     *
     * @param productAttributeId the ID of the ProductAttribute to delete
     * @throws IllegalArgumentException when provided ProductAttribute id is null
     * @throws ResourceNotFoundException if the ProductAttribute with the given ID is not found
     */

    @Transactional
    public void deleteProductAttribute(Long productAttributeId) {
        delete(productAttributeId);
    }

    /**
     * Adds an AttributeValue to a ProductAttribute.
     *
     * @param productAttributeId the ID of the ProductAttribute.
     * @param attributeValueId the ID of the AttributeValue to add.
     * @throws ResourceNotFoundException if the ProductAttribute or AttributeValue does not exist.
     * @throws DuplicateResourceException if the AttributeValue already exists for the ProductAttribute
     */

    @Transactional
    public void addAttributeValue(Long productAttributeId, Long attributeValueId) {
        ProductAttribute productAttribute = productAttributeRepository.findById(productAttributeId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductAttribute with ID " + productAttributeId + " not found."));
        AttributeValue attributeValue = attributeValueService.findById(attributeValueId);

        if (productAttribute.getAttributeValues().contains(attributeValue)) {
            throw new DuplicateResourceException(String.format(
                    "AttributeValue (ID: %d, Value: '%s') is already associated with ProductAttribute (ID: %d, Product: '%s').",
                    attributeValueId, attributeValue.getValue(), productAttributeId, productAttribute.getProduct().getProductName()
            ));
        }

        productAttribute.getAttributeValues().add(attributeValue);
        productAttributeRepository.save(productAttribute);
    }

    /**
     * Removes an AttributeValue from a ProductAttribute.
     *
     * @param productAttributeId the ID of the ProductAttribute.
     * @param attributeValueId the ID of the AttributeValue to remove.
     * @throws ResourceNotFoundException if the ProductAttribute or AttributeValue does not exist.
     */

    @Transactional
    public void removeAttributeValue(Long productAttributeId, Long attributeValueId) {
        ProductAttribute productAttribute = productAttributeRepository.findById(productAttributeId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductAttribute with ID " + productAttributeId + " not found."));
        AttributeValue attributeValue = attributeValueService.findById(attributeValueId);

        if (!productAttribute.getAttributeValues().contains(attributeValue)) {
            throw new ResourceNotFoundException(String.format(
                    "AttributeValue (ID: %d, Value: '%s') is not associated with ProductAttribute (ID: %d, Product: '%s').",
                    attributeValueId, attributeValue.getValue(), productAttributeId, productAttribute.getProduct().getProductName()
            ));
        }

        //only update product attribute if value was actually removed
        if (productAttribute.getAttributeValues().remove(attributeValue)) {
            update(productAttributeId, productAttribute);
        }
    }

}
