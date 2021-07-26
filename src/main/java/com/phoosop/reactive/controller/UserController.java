package com.phoosop.reactive.controller;

import com.phoosop.reactive.component.UserComponent;
import com.phoosop.reactive.model.Response;
import com.phoosop.reactive.model.request.CreateUserRequest;
import com.phoosop.reactive.model.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController implements BaseController {

    private final UserComponent userComponent;
    private final ConversionService conversionService;

    @PostMapping(path = "/v1/users")
    public Mono<Response<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        Mono<UserResponse> userResponseMono = userComponent.createUser(request)
                .map(item -> conversionService.convert(item, UserResponse.class));
        return success(userResponseMono);
    }



}
