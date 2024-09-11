package com.libraryManagementSystem.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CustomValidator.class) // Specifies the validator class
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCustom {
    String message() default "Invalid value"; // Default error message
    Class<?>[] groups() default {}; // Groups for grouping constraints
    Class<? extends Payload>[] payload() default {}; // Additional data to be carried with the annotation
}
