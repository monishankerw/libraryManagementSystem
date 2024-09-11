package com.libraryManagementSystem.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CustomValidator implements ConstraintValidator<ValidCustom, String> {

    @Override
    public void initialize(ValidCustom constraintAnnotation) {
        // Initialization code if needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Custom validation logic
        if (value == null || value.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Value cannot be empty")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
