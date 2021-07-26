package com.phoosop.reactive.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum UserType implements BaseEnum {

    USER("user"),

    MODERATOR("moderator");

    private final String value;

    public static Optional<UserType> find(String value) {
        return Arrays.stream(values())
                .filter(item -> item.getValue().equals(value))
                .findFirst();
    }

}
