package com.phoosop.reactive.annotations;


import com.phoosop.reactive.model.enums.BaseEnum;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static com.phoosop.reactive.exception.StatusConstants.HttpConstants;


@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { IsEnumAnnotation.class })
public @interface IsEnum {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    HttpConstants exception() default HttpConstants.BAD_REQUEST;

    Class<? extends BaseEnum> enumClass();

}
