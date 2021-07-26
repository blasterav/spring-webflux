package com.phoosop.reactive.exception;

import lombok.Getter;

import static com.phoosop.reactive.exception.StatusConstants.HttpConstants;


@Getter
public class NotFoundException extends RuntimeException {

    private final HttpConstants status;

    public NotFoundException(HttpConstants status) {
        super(status.getDesc(), null);
        this.status = status;
    }
}
