package com.phoosop.reactive.annotations;

import com.phoosop.reactive.exception.InvalidRequestException;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.phoosop.reactive.exception.StatusConstants.HttpConstants;

@Component
public class MaxAnnotation implements ConstraintValidator<Max, Object> {

    private HttpConstants exception;
    private long min;

    @Override
    public void initialize(Max constraintAnnotation) {
        this.exception = constraintAnnotation.exception();
        this.min = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value != null) {
            if (value instanceof Integer) {
                if ((Integer) value > min) {
                    throw new InvalidRequestException(exception);
                }
            } else if (value instanceof Long) {
                if ((Long) value > min) {
                    throw new InvalidRequestException(exception);
                }
            }

        }
        return true;
    }

}
