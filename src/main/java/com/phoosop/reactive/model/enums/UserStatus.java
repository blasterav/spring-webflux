package com.phoosop.reactive.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum UserStatus implements BaseEnum {

    REGISTERED(1),

    ACTIVE(2),

    DELETED(3);

    private final Integer value;

    public static Optional<UserStatus> find(int value) {
        return Arrays.stream(values())
                .filter(item -> item.getValue() == value)
                .findFirst();
    }

}
