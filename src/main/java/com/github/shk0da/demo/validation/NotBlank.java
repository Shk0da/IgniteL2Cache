package com.github.shk0da.demo.validation;

import com.github.shk0da.demo.exception.ErrorCode;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = NotBlankValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlank {
    String message() default ErrorCode.Constants.CODE_100;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
