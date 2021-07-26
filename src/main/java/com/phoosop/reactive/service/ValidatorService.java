package com.phoosop.reactive.service;

import com.phoosop.reactive.exception.InvalidRequestException;
import com.phoosop.reactive.model.enums.BaseEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.phoosop.reactive.exception.StatusConstants.HttpConstants;

@Service
public class ValidatorService {

    public static ValidatorService builder() {
        return new ValidatorService();
    }

    public ValidatorService isDate(String date, String pattern, HttpConstants exception) {
        if (date != null) {
            if (!DateTimeUtils.parse(date, pattern).isPresent()) {
                throw new InvalidRequestException(exception);
            }
        }
        return this;
    }

    public ValidatorService isEnum(Object value, Class<? extends BaseEnum> enumClass, HttpConstants exception) {
        List<Object> values = Arrays.stream(enumClass.getEnumConstants()).map(BaseEnum::getValue).collect(Collectors.toList());
        if (value != null && !values.contains(value)) {
            throw new InvalidRequestException(exception);
        }
        return this;
    }

    public ValidatorService max(Object value, long max, HttpConstants exception) {
        if (value != null) {
            if (value instanceof Integer) {
                if ((Integer) value > max) {
                    throw new InvalidRequestException(exception);
                }
            } else if (value instanceof Long) {
                if ((Long) value > max) {
                    throw new InvalidRequestException(exception);
                }
            }

        }
        return this;
    }

    public ValidatorService min(Object value, long min, HttpConstants exception) {
        if (value != null) {
            if (value instanceof Integer) {
                if ((Integer) value < min) {
                    throw new InvalidRequestException(exception);
                }
            } else if (value instanceof Long) {
                if ((Long) value < min) {
                    throw new InvalidRequestException(exception);
                }
            }

        }
        return this;
    }

    public ValidatorService required(Object value, HttpConstants exception) {
        if (value == null) {
            throw new InvalidRequestException(exception);
        } else if (value instanceof String && StringUtils.isBlank((String) value)) {
            throw new InvalidRequestException(exception);
        }
        return this;
    }

}
