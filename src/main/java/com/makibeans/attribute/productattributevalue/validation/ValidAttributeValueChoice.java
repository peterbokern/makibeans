package com.makibeans.attribute.productattributevalue.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProductAttributeValueChoiceValidator.class)
public @interface ValidAttributeValueChoice {

    String message() default "Invalid attribute value choice.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
