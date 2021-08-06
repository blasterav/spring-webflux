package com.phoosop.reactive.controller;

import com.phoosop.reactive.component.UserComponent;
import com.phoosop.reactive.model.CustomPage;
import com.phoosop.reactive.model.Response;
import com.phoosop.reactive.model.request.CreateUserRequest;
import com.phoosop.reactive.model.request.UpdateUserRequest;
import com.phoosop.reactive.model.response.UserResponse;
import com.phoosop.reactive.model.response.UserShortResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController implements BaseController {

    private final UserComponent userComponent;

    @PostMapping(path = "/v1/users")
    public Mono<Response<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        Mono<UserResponse> userResponseMono = userComponent.createUser(request);
        return success(userResponseMono);
    }

    @PatchMapping(path = "/v1/users/{id}")
    public Mono<Response<UserResponse>> updateUser(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateUserRequest request) {
        Mono<UserResponse> userResponseMono = userComponent.updateUser(id, request);
        return success(userResponseMono);
    }

    @GetMapping(path = "/v1/users/{id}")
    public Mono<Response<UserResponse>> getUser(@PathVariable Long id) {
        Mono<UserResponse> userResponseMono = userComponent.getUser(id);
        return success(userResponseMono);
    }

    @GetMapping(path = "/v1/users")
    public Mono<Response<CustomPage<UserShortResponse>>> getUserList(@RequestParam(required = false, defaultValue = "1") Integer page,
                                                                     @RequestParam(required = false, defaultValue = "10") Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Mono<CustomPage<UserShortResponse>> pageMono = userComponent.getUserList(pageRequest);
        return success(pageMono);
    }

    @DeleteMapping(path = "/v1/users/{id}")
    public Mono<Response<Void>> deleteUser(@PathVariable Long id) {
        userComponent.deleteUser(id);
        return success();
    }

}
