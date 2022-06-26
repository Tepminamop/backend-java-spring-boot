package com.backend.backend.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = PostValidator.class)
@Documented
public @interface PostConstraint {

    String message() default "{PostConstraint.invalid}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
