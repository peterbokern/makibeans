package com.makibeans.productattributevalue.validation;

import com.makibeans.attribute.model.AttributeDataType;
import com.makibeans.attribute.model.AttributeInputType;
import com.makibeans.attributevalue.repository.AttributeValueRepository;
import com.makibeans.productattribute.model.ProductAttribute;
import com.makibeans.productattribute.repository.ProductAttributeRepository;
import com.makibeans.productattributevalue.dto.ProductAttributeValueRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Component
public class ProductAttributeValueChoiceValidator
        implements ConstraintValidator<ValidAttributeValueChoice, ProductAttributeValueRequestDTO> {

    private final ProductAttributeRepository productAttributeRepository;
    private final AttributeValueRepository attributeValueRepository;

    public ProductAttributeValueChoiceValidator(
            ProductAttributeRepository productAttributeRepository,
            AttributeValueRepository attributeValueRepository
    ) {
        this.productAttributeRepository = productAttributeRepository;
        this.attributeValueRepository = attributeValueRepository;
    }

    @Override
    public boolean isValid(ProductAttributeValueRequestDTO dto, ConstraintValidatorContext ctx) {
        if (dto == null) return true; // let @NotNull handle if needed

        // If productAttributeId missing, let @NotNull handle (avoid double messages)
        if (dto.productAttributeId() == null) return true;

        ProductAttribute pa = productAttributeRepository
                .findById(dto.productAttributeId())
                .orElse(null);

        if (pa == null) return violation(ctx, "productAttributeId", "ProductAttribute does not exist.");


        var ca = pa.getCategoryAttribute();
        if (ca == null) return violation(ctx, null, "ProductAttribute is missing CategoryAttribute.");

        AttributeInputType inputType = ca.getAttribute().getInputType();
        AttributeDataType dataType = ca.getAttribute().getDataType();

        Long attributeValueId = dto.attributeValueId();
        String rawValue = dto.rawValue() != null ? dto.rawValue().trim() : null;

        boolean hasAttributeValueId = attributeValueId != null;
        boolean hasRawValue = rawValue != null && !rawValue.isBlank();

        // Must provide exactly one
        if (hasAttributeValueId && hasRawValue) {
            // both true OR both false
            return violation(ctx, "attributeValueId",
                    "Provide exactly one of 'attributeValueId' or 'rawValue' based on input type.");
        }

        boolean libraryBacked = inputType != null && inputType.isLibraryBacked();

        if (libraryBacked) {
            // Must use attributeValueId, rawValue must be null/blank
            if (!hasAttributeValueId) {
                return violation(ctx, "attributeValueId",
                        "This attribute requires a predefined value (attributeValueId).");
            }

            // Validate that AttributeValue exists and belongs to the same Attribute as the CategoryAttribute
            Long expectedAttributeId = ca.getAttribute().getId();

            boolean ok = attributeValueRepository
                    .existsByIdAndAttributeIdAndDeletedFalse(attributeValueId, expectedAttributeId);

            if (!ok) {
                return violation(ctx, "attributeValueId",
                        "Selected value is not a valid option for this attribute.");
            }

            return true;
        }

        // Not library-backed: must use rawValue, attributeValueId must be null
        if (!hasRawValue) {
            return violation(ctx, "rawValue", "A raw value is required for this attribute.");
        }

        // Validate rawValue matches data type
        return validateRawValueType(rawValue, dataType, ctx);
    }

    private boolean validateRawValueType(String rawValue, AttributeDataType dataType, ConstraintValidatorContext ctx) {
        if (dataType == null) return true;

        try {
            switch (dataType) {
                case NUMBER -> {
                    new BigDecimal(rawValue);
                    return true;
                }
                case BOOLEAN -> {
                    // Accept strict boolean tokens
                    if (!rawValue.equalsIgnoreCase("true") && !rawValue.equalsIgnoreCase("false")) {
                        return violation(ctx, "rawValue", "Invalid boolean value. Use 'true' or 'false'.");
                    }
                    return true;
                }
                case DATE -> {
                    // ISO-8601 date, e.g. 2025-12-16
                    LocalDate.parse(rawValue);
                    return true;
                }
                case DATETIME -> {
                    // ISO-8601 datetime, e.g. 2025-12-16T14:35:19Z
                    OffsetDateTime.parse(rawValue);
                    return true;
                }
                default -> {
                    return true;
                }
            }
        } catch (Exception ex) {
            return violation(ctx, "rawValue",
                    "Invalid value for type " + dataType + ".");
        }
    }

    private boolean violation(ConstraintValidatorContext ctx, String field, String message) {
        ctx.disableDefaultConstraintViolation();

        if (field == null || field.isBlank()) {
            ctx.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        } else {
            ctx.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(field)
                    .addConstraintViolation();
        }
        return false;
    }
}
