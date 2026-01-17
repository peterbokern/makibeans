package com.makibeans.attribute.service;

import com.makibeans.attribute.dto.AttributeUsageDTO;
import com.makibeans.attribute.model.Attribute;
import com.makibeans.attributevalue.repository.AttributeValueRepository;
import com.makibeans.categoryattribute.repository.CategoryAttributeRepository;
import com.makibeans.productattribute.repository.ProductAttributeRepository;
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
    public AttributeUsageDTO summarizeUsage(Attribute attribute) {
        boolean usedInValues = attributeValueRepository.existsByAttributeAndDeletedFalse(attribute);
        boolean usedInCategoryAttributes = categoryAttributeRepository.existsByAttributeAndDeletedFalse(attribute);

        return new AttributeUsageDTO(
                usedInValues,
                usedInCategoryAttributes
        );
    }

    @Transactional(readOnly = true)
    public boolean isInUse(Attribute attribute) {
        AttributeUsageDTO usageSummary = summarizeUsage(attribute);
        return usageSummary.inUse();
    }

    @Transactional(readOnly = true)
    public String getUsageDetails(Attribute attribute) {
        AttributeUsageDTO usageSummary = summarizeUsage(attribute);
        if (!usageSummary.inUse()) {
            return "Not referenced.";
        }

        List<String> usedIn = new ArrayList<>();
        if (usageSummary.usedInValues()) {
            usedIn.add("Attribute Values");
        }

        if (usageSummary.usedInCategoryAttributes()) {
            usedIn.add("Category Attributes");
        }

        return "Referenced by: " + TextUtils.toCommaDelimitedString(usedIn);
    }


}
