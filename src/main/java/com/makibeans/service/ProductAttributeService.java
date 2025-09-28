package com.makibeans.service;

import com.makibeans.dto.productattribute.ProductAttributeRequestDTO;
import com.makibeans.dto.productattribute.ProductAttributeResponseDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.mapper.ProductAttributeMapper;
import com.makibeans.model.*;
import com.makibeans.repository.ProductAttributeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing ProductAttributes.
 */

@Service
public class ProductAttributeService extends AbstractCrudService<ProductAttribute, Long>{

    private final ProductAttributeRepository productAttributeRepository;
    private final ProductService productService;
    private final AttributeService attributeService;
    private final ProductAttributeMapper productAttributeMapper;
    private final AttributeValueService attributeValueService;

    @Autowired
    public ProductAttributeService(JpaRepository<ProductAttribute, Long> repository, ProductAttributeRepository productAttributeRepository, ProductService productService, AttributeService attributeService, ProductAttributeMapper productAttributeMapper, AttributeValueService attributeValueService) {
        super(repository);
        this.productAttributeRepository = productAttributeRepository;
        this.productService = productService;
        this.attributeService = attributeService;
        this.productAttributeMapper = productAttributeMapper;
        this.attributeValueService = attributeValueService;
    }

    /**
     * Retrieves a product attribute by its ID.
     *
     * @param id the ID of the productAttribute to retrieve.
     * @return the ProductAttributeResponseDTO representing the product attribute.
     * @throws IllegalArgumentException if the id is null.
     * @throws ResourceNotFoundException if the product attribute does not exist.
     */

    @Transactional(readOnly = true)
    public ProductAttributeResponseDTO getProductAttributeById(Long id) {
        ProductAttribute productAttribute = findById(id);
        return productAttributeMapper.toResponseDTO(productAttribute);
    }

    /**
     * Retrieves all product attributes.
     *
     * @return a list of productAttributeResponseDTO's representing all product attributes..
     */

    @Transactional(readOnly = true)
    public List<ProductAttributeResponseDTO> getAllProductAttributes(){
        return productAttributeRepository.findAll().stream().map(productAttributeMapper:: toResponseDTO).toList();
    }

    /**
     * Retrieves a list of ProductAttributes by the given Attribute ID.
     *
     * @param templateId the ID of the Attribute.
     * @return a list of ProductAttributes associated with the given Attribute ID.
     * @throws IllegalArgumentException if the templateId is null.
     */

    @Transactional(readOnly = true)
    public List<ProductAttribute> getProductAttributesByTemplateId(Long templateId) {
        return productAttributeRepository.findByAttributeId(templateId);
    }

    /**
     * Retrieves a list of ProductAttributes by the given Product ID.
     *
     * @param productId the ID of the Product.
     * @return a list of ProductAttributes associated with the given Product ID.
     * @throws IllegalArgumentException if the productId is null.
     */

    @Transactional
    public List<ProductAttribute> getProductAttributesByProductId(Long productId) {
        return productAttributeRepository.findByProductId(productId);
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
        Long templateId = requestDTO.getAttributeId();

        Product product = productService.findById(productId);
        Attribute attribute = attributeService.findById(templateId);

        validateUniqueProductAttribute(productId, templateId);

        ProductAttribute productAttribute = new ProductAttribute(attribute, product);
        ProductAttribute savedProductAttribute = create(productAttribute);

        return productAttributeMapper.toResponseDTO(savedProductAttribute);
    }

    /**
     * Deletes an ProductAttribute  by ID and all associated attribute values.
     *
     * @param productAttributeId the ID of the ProductAttribute to delete
     * @throws IllegalArgumentException when provided ProductAttribute id is null
     * @throws ResourceNotFoundException if the ProductAttribute with the given ID is not found
     */

    @Transactional
    public void deleteProductAttribute(Long productAttributeId) {
        findById(productAttributeId);
        //REMOVE not required as orphanRemoval = true is set on the relationship in ProductAttribute entity
        //productAttributeRepository.deleteAttributeValuesByProductAttributeId(productAttributeId);
        delete(productAttributeId);
    }

    /**
     * Deletes attribute values by attribute value ID.
     *
     * @param attributeValueId the ID of the attribute value to delete
     */

    @Transactional
    public void deleteAttributeValuesByAttributeValueId(Long attributeValueId) {
        productAttributeRepository.deleteAttributeValuesByAttributeValueId(attributeValueId);
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
        ProductAttribute productAttribute = findById(productAttributeId);

        AttributeValue attributeValue = attributeValueService.findById(attributeValueId);

        // Ensure the AttributeValue's template matches the ProductAttribute's template
        if(!productAttribute.getAttribute().getId().equals(attributeValue.getAttribute().getId())){
            throw new IllegalArgumentException("Attribute Value's template does not match Product Attribute's template.");
        }

        validateAttributeValueNotAlreadyAssociated(productAttribute, attributeValue);

        ProductAttributeValue link = new ProductAttributeValue(attributeValue, productAttribute);

        productAttribute.getProductAttributeValueLinks().add(link);

        update(productAttributeId, productAttribute);
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
        ProductAttribute productAttribute = findById(productAttributeId);

        AttributeValue attributeValue = attributeValueService.findById(attributeValueId);

        validateAttributeValueAssociation(productAttribute, attributeValue);

        productAttribute.getProductAttributeValueLinks().removeIf(link -> link.getAttributeValue().getId().equals(attributeValueId));

        update(productAttributeId, productAttribute);
    }

    /**
     * Validates if an AttributeValue is associated with a ProductAttribute.
     *
     * @param productAttribute the ProductAttribute to check.
     * @param attributeValue the AttributeValue to check.
     * @throws ResourceNotFoundException if the AttributeValue is not associated with the ProductAttribute.
     */

    private void validateAttributeValueAssociation(ProductAttribute productAttribute, AttributeValue attributeValue) {
        if (productAttribute.getProductAttributeValueLinks().stream().noneMatch(link -> link.getAttributeValue().equals(attributeValue))) {
            throw new ResourceNotFoundException(String.format(
                    "AttributeValue (ID: %d, Value: '%s') is not associated with ProductAttribute (ID: %d, Product: '%s').",
                    attributeValue.getId(),
                    attributeValue.getValue(),
                    productAttribute.getId(),
                    productAttribute.getProduct().getName()));
        }
    }

    /**
     * Validates that an AttributeValue is not already associated with a ProductAttribute.
     *
     * @param productAttribute the ProductAttribute to check.
     * @param attributeValue the AttributeValue to check.
     * @throws DuplicateResourceException if the AttributeValue is already associated.
     */

    private void validateAttributeValueNotAlreadyAssociated(ProductAttribute productAttribute, AttributeValue attributeValue) {
        if (productAttribute.getProductAttributeValueLinks().stream().anyMatch(link -> link.getAttributeValue().equals(attributeValue))) {
            throw new DuplicateResourceException(String.format(
                    "AttributeValue (ID: %d, Value: '%s') is already associated with ProductAttribute (ID: %d, Product: '%s').",
                    attributeValue.getId(),
                    attributeValue.getValue(),
                    productAttribute.getId(),
                    productAttribute.getProduct().getName()));
        }
    }

    /**
     * Validates the existence of a ProductAttribute by productId and templateId.
     *
     * @param productId the ID of the product.
     * @param templateId the ID of the attribute template.
     * @throws DuplicateResourceException if a ProductAttribute with the given productId and templateId already exists.
     */

    private void validateUniqueProductAttribute(Long productId, Long templateId) {
        if (productAttributeRepository.existsByProductIdAndAttributeId(productId, templateId)) {
            throw new DuplicateResourceException("Product Attribute with product id " + productId + " and template id " + templateId + " already exists.");
        }
    }
}
