package com.makibeans.attribute.attribute.service;

import com.makibeans.attribute.attribute.dto.AttributeUsageDTO;
import com.makibeans.attribute.attributevalue.repository.AttributeValueRepository;
import com.makibeans.attribute.categoryattribute.repository.CategoryAttributeRepository;
import com.makibeans.attribute.productattribute.repository.ProductAttributeRepository;
import com.makibeans.common.util.TextUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AttributeUsageChecker {

    private final AttributeValueRepository attributeValueRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final CategoryAttributeRepository categoryAttributeRepository;

    public AttributeUsageChecker(
            AttributeValueRepository attributeValueRepository,
            ProductAttributeRepository productAttributeRepository,
            CategoryAttributeRepository categoryAttributeRepository) {
        this.attributeValueRepository = attributeValueRepository;
        this.productAttributeRepository = productAttributeRepository;
        this.categoryAttributeRepository = categoryAttributeRepository;
    }

    @Transactional(readOnly = true)
    public AttributeUsageDTO summarizeUsage(Long attributeId) {
        boolean usedInValues = attributeValueRepository.existsByAttributeIdAndDeletedFalse(attributeId);
        boolean usedInProductAttributes = productAttributeRepository.existsByAttributeIdAndDeletedFalse(attributeId);
        boolean usedInCategoryAttributes = categoryAttributeRepository.existsByAttributeIdAndDeletedFalse(attributeId);

        return new AttributeUsageDTO(
                usedInValues,
                usedInProductAttributes,
                usedInCategoryAttributes
        );
    }

    @Transactional(readOnly = true)
    public boolean isInUse(Long attributeId) {
        AttributeUsageDTO usageSummary = summarizeUsage(attributeId);
        return usageSummary.inUse();
    }

    @Transactional(readOnly = true)
    public String getUsageDetails(Long attributeId) {
        AttributeUsageDTO usageSummary = summarizeUsage(attributeId);
        if (!usageSummary.inUse()) {
            return "Not referenced.";
        }

        List<String> usedIn = new ArrayList<>();
        if (usageSummary.usedInValues()) {
            usedIn.add("Attribute Values");
        }
        if (usageSummary.usedInProductAttributes()) {
            usedIn.add("Product Attributes");
        }
        if (usageSummary.usedInCategoryAttributes()) {
            usedIn.add("Category Attributes");
        }

        return "Referenced by: " + TextUtils.toCommaDelimitedString(usedIn);
    }


}
