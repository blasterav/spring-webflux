package com.phoosop.reactive.exception;

import lombok.Getter;

import static com.phoosop.reactive.exception.StatusConstants.*;


@Getter
public class InvalidRequestException extends RuntimeException {

    private final HttpConstants status;

    public InvalidRequestException(HttpConstants status) {
        super(status.getDesc(), null);
        this.status = status;
    }

}
