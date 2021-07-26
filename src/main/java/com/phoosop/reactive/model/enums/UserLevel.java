package com.phoosop.reactive.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum UserLevel implements BaseEnum {

    LEVEL_1(1),

    LEVEL_2(2);

    private final Integer value;

    public static Optional<UserLevel> find(int value) {
        return Arrays.stream(values())
                .filter(item -> item.getValue() == value)
                .findFirst();
    }

}
