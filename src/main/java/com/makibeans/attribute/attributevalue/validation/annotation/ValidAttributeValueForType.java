package com.makibeans.attribute.attributevalue.validation.annotation;

import com.makibeans.attribute.attributevalue.validation.validator.AttributeValueTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AttributeValueTypeValidator.class)
public @interface ValidAttributeValueForType {
    String message() default "Invalid attribute value for data  type.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
