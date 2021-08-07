package com.phoosop.reactive.annotations;

import com.phoosop.reactive.exception.InvalidRequestException;
import com.phoosop.reactive.util.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.phoosop.reactive.exception.StatusConstants.HttpConstants;

@Component
public class IsDateAnnotation implements ConstraintValidator<IsDate, String> {

    private HttpConstants exception;

    private String format;

    @Override
    public void initialize(IsDate constraintAnnotation) {
        this.exception = constraintAnnotation.exception();
        this.format = constraintAnnotation.format();
    }

    @Override
    public boolean isValid(String date, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isNotBlank(date)) {
            if (DateTimeUtils.parse(date, format).isPresent()) {
                return true;
            }
            throw new InvalidRequestException(exception);
        }
        return true;
    }

}
