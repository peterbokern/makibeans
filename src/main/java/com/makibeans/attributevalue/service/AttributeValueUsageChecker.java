package com.makibeans.attributevalue.service;

import com.makibeans.attributevalue.dto.AttributeValueUsageDTO;
import com.makibeans.attributevalue.model.AttributeValue;
import com.makibeans.productattributevalue.repository.ProductAttributeValueRepository;
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
