package com.phoosop.reactive.controller;


import com.phoosop.reactive.exception.StatusConstants;
import com.phoosop.reactive.model.Response;
import com.phoosop.reactive.model.Status;
import reactor.core.publisher.Mono;

import static com.phoosop.reactive.exception.StatusConstants.HttpConstants;

public interface BaseController {

    default <T> Mono<Response<T>> success() {
        return Mono.just(new Response<>(new Status(HttpConstants.SUCCESS), null));
    }

    default <T> Mono<Response<T>> success(Mono<T> data) {
        return data.map(item -> new Response<>(new Status(StatusConstants.HttpConstants.SUCCESS), item));
    }

}
