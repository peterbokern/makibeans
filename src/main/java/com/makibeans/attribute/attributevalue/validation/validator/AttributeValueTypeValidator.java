package com.makibeans.attribute.attributevalue.validation.validator;

import com.makibeans.attribute.attributevalue.model.AttributeValue;
import com.makibeans.attribute.attributevalue.validation.annotation.ValidAttributeValueForType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AttributeValueTypeValidator implements ConstraintValidator<ValidAttributeValueForType, AttributeValue> {

    @Override
    public boolean isValid(AttributeValue attributeValue, ConstraintValidatorContext context) {

        //Consider null as valid, use @NotNull for null checks
        if (attributeValue == null || attributeValue.getAttribute() == null || attributeValue.getAttribute().getDataType() == null) {
            return true;
        }

        var dataType = attributeValue.getAttribute().getDataType();

        boolean matches = switch (dataType) {
            case STRING -> attributeValue.getStringValue() != null;
            case NUMERIC -> attributeValue.getNumericValue() != null;
            case BOOLEAN -> attributeValue.getBooleanValue() != null;
            case DATE -> attributeValue.getDateValue() != null;
            case DATETIME -> attributeValue.getDateTimeValue() != null;
        };

        if (!matches)
            buildViolation(context, "Attribute value does not match the expected data type: " + dataType.getLabel());

        int nonNullCount = 0;
        if (attributeValue.getStringValue() != null) nonNullCount++;
        if (attributeValue.getNumericValue() != null) nonNullCount++;
        if (attributeValue.getBooleanValue() != null) nonNullCount++;
        if (attributeValue.getDateValue() != null) nonNullCount++;
        if (attributeValue.getDateTimeValue() != null) nonNullCount++;

        if (nonNullCount == 0) {
            return buildViolation(context, "At least one value must be provided for the attribute.");
        }

        if (nonNullCount > 1) {
            return buildViolation(context, "Only one value must be provided for the attribute.");
        }

        return true;
    }

    private boolean buildViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }
}
