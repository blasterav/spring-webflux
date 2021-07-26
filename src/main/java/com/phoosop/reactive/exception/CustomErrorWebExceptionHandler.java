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
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
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

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);
        if (error instanceof ServiceException) {
            ServiceException exception = (ServiceException) error;
            LOG.error("Failed {} {}: {}, {}", request.methodName(), request.uri().getPath(), exception.getStatus().getCode(), exception.getStatus().getDesc());
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(new Response<>(new Status(exception.getStatus()), null)));
        } else if (error instanceof InvalidRequestException) {
            InvalidRequestException exception = (InvalidRequestException) error;
            LOG.error("Failed {} {}: {}, {}", request.methodName(), request.uri().getPath(), exception.getStatus().getCode(), exception.getStatus().getDesc());
            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(new Response<>(new Status(exception.getStatus()), null)));
        } else if (error instanceof ValidationException) {
            InvalidRequestException exception = (InvalidRequestException) error.getCause();
            LOG.error("Failed {} {}: {}, {}", request.methodName(), request.uri().getPath(), exception.getStatus().getCode(), exception.getStatus().getDesc());
            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(new Response<>(new Status(exception.getStatus()), null)));
        } else if (error instanceof NotFoundException) {
            NotFoundException exception = (NotFoundException) error;
            LOG.error("Failed {} {}: {}, {}", request.methodName(), request.uri().getPath(), exception.getStatus().getCode(), exception.getStatus().getDesc());
            return ServerResponse.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(new Response<>(new Status(exception.getStatus()), null)));
        } else if (error instanceof ConversionFailedException) {
            ServiceException exception = (ServiceException) error.getCause();
            LOG.error("Failed {} {}: {}, {}", request.methodName(), request.uri().getPath(), exception.getStatus().getCode(), exception.getStatus().getDesc());
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(new Response<>(new Status(exception.getStatus()), null)));
        } else {
            LOG.error("Failed {} {}: {}", request.methodName(), request.uri().getPath(), error.getMessage());
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(new Response<>(new Status(HttpConstants.INTERNAL_SERVER_ERROR), null)));
        }

    }
}
