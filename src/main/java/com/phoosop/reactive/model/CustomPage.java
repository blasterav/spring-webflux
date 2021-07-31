package com.phoosop.reactive.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CustomPage<T> {

    private List<T> content;

    private int number;

    private int size;

    private long totalElements;

    public <U> CustomPage<U> map(Function<? super T, ? extends U> converter) {
        return new CustomPage<U>(this.content.stream().map(converter).collect(Collectors.toList()), this.number, this.size, this.totalElements);
    }

}
