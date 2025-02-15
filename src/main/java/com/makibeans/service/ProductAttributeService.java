package com.makibeans.service;

import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.model.AttributeTemplate;
import com.makibeans.model.Product;
import com.makibeans.model.ProductAttribute;
import com.makibeans.repository.ProductAttributeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductAttributeService extends AbstractCrudService<ProductAttribute, Long>{

    private final ProductAttributeRepository productAttributeRepository;
    private final ProductService productService;
    private final AttributeTemplateService attributeTemplateService;

    @Autowired
    public ProductAttributeService(JpaRepository<ProductAttribute, Long> repository, ProductAttributeRepository productAttributeRepository, ProductService productService, AttributeTemplateService attributeTemplateService) {
        super(repository);
        this.productAttributeRepository = productAttributeRepository;
        this.productService = productService;
        this.attributeTemplateService = attributeTemplateService;
    }

    /**
     * Creates a product attribute.
     *
     * @param productId the ID of the product
     * @param templateId the ID of the attribute template
     * @return the newly created product attribute
     * @throws IllegalArgumentException if productId or templateId are null
     * @throws DuplicateResourceException if a product attribute already exists
     */

    @Transactional
    public ProductAttribute createProductAttribute(Long productId, Long templateId) {

        if (productId == null) {
            throw new IllegalArgumentException("Product id cannot be null.");
        }

        if (templateId == null) {
            throw new IllegalArgumentException("Template id cannot be null.");
        }

        Product product = productService.findById(productId);
        AttributeTemplate attributeTemplate = attributeTemplateService.findById(templateId);

        if (productAttributeRepository.existsByProductIdAndAttributeTemplateId(productId, templateId)) {
            throw new DuplicateResourceException("Product Attribute with product id " + productId + " and template id " + templateId + " already exists.");
        }

        return create(new ProductAttribute(attributeTemplate, product));
    }

    //delete

    //update
}
