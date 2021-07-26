package com.phoosop.reactive.annotations;

import com.phoosop.reactive.exception.StatusConstants;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static com.phoosop.reactive.exception.StatusConstants.*;


@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { RequiredAnnotation.class })
public @interface Required {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    HttpConstants exception() default HttpConstants.BAD_REQUEST;

}
