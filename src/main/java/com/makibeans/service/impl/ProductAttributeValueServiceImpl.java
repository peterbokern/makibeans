package com.makibeans.service.impl;

import com.makibeans.dto.productattributevalue.ProductAttributeValueRequestDTO;
import com.makibeans.dto.productattributevalue.ProductAttributeValueResponseDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.mapper.ProductAttributeValueMapper;
import com.makibeans.model.AttributeValue;
import com.makibeans.model.ProductAttribute;
import com.makibeans.model.ProductAttributeValue;
import com.makibeans.repository.ProductAttributeValueRepository;
import com.makibeans.service.service.AttributeValueService;
import com.makibeans.service.service.CrudService;
import com.makibeans.service.service.ProductAttributeService;
import com.makibeans.service.service.ProductAttributeValueService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductAttributeValueServiceImpl implements CrudService<ProductAttributeValue, Long>, ProductAttributeValueService {

    private final ProductAttributeValueRepository repo;
    private final ProductAttributeService productAttributeService;
    private final AttributeValueService attributeValueService;
    private final ProductAttributeValueMapper mapper;

    public ProductAttributeValueServiceImpl(ProductAttributeValueRepository repo, ProductAttributeService productAttributeService, AttributeValueService attributeValueService, ProductAttributeValueMapper mapper) {
        this.repo = repo;
        this.productAttributeService = productAttributeService;
        this.attributeValueService = attributeValueService;
        this.mapper = mapper;
    }

    /**
     * Implementors must return their repository.
     */
    @Override
    public JpaRepository<ProductAttributeValue, Long> repo() {
        return this.repo;
    }

    @Override
    public ProductAttributeValueResponseDTO getById(Long id) {
        ProductAttributeValue entity = getOrThrow(id);
        return mapper.toResponseDTO(entity);
    }

    @Override
    public ProductAttributeValueResponseDTO create(ProductAttributeValueRequestDTO dto) {
        ProductAttributeValue entity = new ProductAttributeValue();
        ProductAttribute productAttribute = productAttributeService.getOrThrow(dto.getProductAttributeId());
        AttributeValue attributeValue = attributeValueService.getOrThrow(dto.getAttributeValueId());
        if (repo.existsByProductAttributeIdAndAttributeValueId(dto.getProductAttributeId(), dto.getAttributeValueId())) {
            throw new DuplicateResourceException("Duplicate ProductAttributeValue link");
        }

        entity.setProductAttribute(productAttribute);
        entity.setAttributeValue(attributeValue);
        ProductAttributeValue saved = repo.save(entity);
        return mapper.toResponseDTO(saved);
    }

    @Override
    public void delete(Long id) {
        hardDelete(id);
    }
}
