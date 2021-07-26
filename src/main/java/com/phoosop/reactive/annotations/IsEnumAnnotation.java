package com.phoosop.reactive.annotations;

import com.phoosop.reactive.exception.InvalidRequestException;
import com.phoosop.reactive.exception.StatusConstants;
import com.phoosop.reactive.model.enums.BaseEnum;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.phoosop.reactive.exception.StatusConstants.*;

@Component
public class IsEnumAnnotation implements ConstraintValidator<IsEnum, Object> {

    private HttpConstants exception;
    private Class<? extends BaseEnum> enumClass;
    private List<Object> values;

    @Override
    public void initialize(IsEnum constraintAnnotation) {
        this.exception = constraintAnnotation.exception();
        this.enumClass = constraintAnnotation.enumClass();
        this.values = Arrays.stream(enumClass.getEnumConstants()).map(BaseEnum::getValue).collect(Collectors.toList());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value != null && !values.contains(value)) {
            throw new InvalidRequestException(exception);
        }
        return true;
    }

}
