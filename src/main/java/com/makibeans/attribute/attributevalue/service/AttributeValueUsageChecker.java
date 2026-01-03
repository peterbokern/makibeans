package com.makibeans.attribute.attributevalue.service;

import com.makibeans.attribute.attributevalue.dto.AttributeValueUsageDTO;
import com.makibeans.attribute.attributevalue.model.AttributeValue;
import com.makibeans.attribute.productattributevalue.repository.ProductAttributeValueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AttributeValueUsageChecker {

    private final ProductAttributeValueRepository repo;

    public AttributeValueUsageChecker(ProductAttributeValueRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public AttributeValueUsageDTO summarizeUsage(AttributeValue value) {
        boolean usedInProductAttributeValues = repo.existsByAttributeValueAndDeletedFalse(value);
        return new AttributeValueUsageDTO(usedInProductAttributeValues);
    }

    @Transactional(readOnly = true)
    public boolean isInUse(AttributeValue value) {
        AttributeValueUsageDTO usageSummary = summarizeUsage(value);
        return usageSummary.inUse();
    }

    // for exception messages
    @Transactional(readOnly = true)
    public String getUsageDetails(AttributeValue value) {
        AttributeValueUsageDTO usageSummary = summarizeUsage(value);
        return !usageSummary.inUse() ? "Not referenced." : "Referenced in Product Attribute Values.";

    }
}
