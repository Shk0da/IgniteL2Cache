package com.github.shk0da.demo.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotBlankValidator implements ConstraintValidator<NotBlank, String> {

    private static final NotBlankValidator INSTANCE = new NotBlankValidator();

    public static boolean isValid(String value) {
        return INSTANCE.isValid(value, null);
    }

    @Override
    public void initialize(NotBlank value) {
        // initialize
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext cxt) {
        return value == null || !value.isEmpty();
    }
}
