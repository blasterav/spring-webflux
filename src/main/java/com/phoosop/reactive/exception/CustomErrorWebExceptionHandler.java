package com.phoosop.reactive.exception;

import com.phoosop.reactive.model.Response;
import com.phoosop.reactive.model.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.validation.ValidationException;

import static com.phoosop.reactive.exception.StatusConstants.HttpConstants;

@Component
@Order(-2)
public class CustomErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    private final Logger LOG = LoggerFactory.getLogger(CustomErrorWebExceptionHandler.class);

    public CustomErrorWebExceptionHandler(ErrorAttributes errorAttributes, ApplicationContext applicationContext, ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, new WebProperties.Resources(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    @Override
    protected void logError(ServerRequest request, ServerResponse response, Throwable throwable) {

    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable throwable = getError(request);
        if (throwable instanceof ServiceException exception) {
            LOG.error("Failed {} {}: {}, {}", request.methodName(), request.uri().getPath(), exception.getStatus().getCode(), exception.getStatus().getDesc());
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(new Response<>(new Status(exception.getStatus()), null)));

        } else if (throwable instanceof InvalidRequestException exception) {
            LOG.error("Failed {} {}: {}, {}", request.methodName(), request.uri().getPath(), exception.getStatus().getCode(), exception.getStatus().getDesc());
            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(new Response<>(new Status(exception.getStatus()), null)));

        } else if (throwable instanceof NotFoundException exception) {
            LOG.error("Failed {} {}: {}, {}", request.methodName(), request.uri().getPath(), exception.getStatus().getCode(), exception.getStatus().getDesc());
            return ServerResponse.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(new Response<>(new Status(exception.getStatus()), null)));

        } else if (throwable instanceof ValidationException
                && throwable.getCause() instanceof InvalidRequestException exception) {
            LOG.error("Failed {} {}: {}, {}", request.methodName(), request.uri().getPath(), exception.getStatus().getCode(), exception.getStatus().getDesc());
            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(new Response<>(new Status(exception.getStatus()), null)));

        } else if (throwable instanceof ConversionFailedException
                && throwable.getCause() instanceof ServiceException exception) {
            LOG.error("Failed {} {}: {}, {}", request.methodName(), request.uri().getPath(), exception.getStatus().getCode(), exception.getStatus().getDesc());
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(new Response<>(new Status(exception.getStatus()), null)));

        } else if (throwable instanceof MethodNotAllowedException) {
            LOG.error("Failed {} {}: {}", request.methodName(), request.uri().getPath(), throwable.getMessage());
            return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(new Response<>(new Status(HttpConstants.METHOD_NOT_ALLOWED), null)));

        } else if (throwable instanceof ResponseStatusException exception) {
            LOG.error("Failed {} {}: {}", request.methodName(), request.uri().getPath(), throwable.getMessage());
            if (exception.getReason() != null && exception.getReason().equals("No matching handler")) {
                return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(new Response<>(new Status(HttpConstants.NO_MATCHING_HANDLER), null)));
            }
            if (exception.getCause() instanceof DecodingException) {
                return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(new Response<>(new Status(HttpConstants.JSON_DECODING_ERROR), null)));
            }
            return this.internalServerError(request, throwable);
        } else {
            return this.internalServerError(request, throwable);
        }

    }

    private Mono<ServerResponse> internalServerError(ServerRequest request, Throwable error) {
        LOG.error("Failed {} {}: {}", request.methodName(), request.uri().getPath(), error.getMessage());
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new Response<>(new Status(HttpConstants.INTERNAL_SERVER_ERROR), null)));
    }
}

