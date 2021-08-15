package com.phoosop.reactive.component;

import com.phoosop.reactive.exception.NotFoundException;
import com.phoosop.reactive.exception.ServiceException;
import com.phoosop.reactive.exception.StatusConstants.HttpConstants;
import com.phoosop.reactive.model.CustomPage;
import com.phoosop.reactive.model.command.UserCommand;
import com.phoosop.reactive.model.enums.UserLevel;
import com.phoosop.reactive.model.enums.UserStatus;
import com.phoosop.reactive.model.enums.UserType;
import com.phoosop.reactive.model.request.CreateUserRequest;
import com.phoosop.reactive.model.request.UpdateUserRequest;
import com.phoosop.reactive.model.response.ActivityResponse;
import com.phoosop.reactive.model.response.UserResponse;
import com.phoosop.reactive.model.response.UserShortResponse;
import com.phoosop.reactive.service.persistence.UserPersistenceService;
import com.phoosop.reactive.service.webclient.BoredapiClientService;
import com.phoosop.reactive.util.CryptoUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Base64;


@Component
@RequiredArgsConstructor
public class UserComponent {

    private final UserPersistenceService userPersistenceService;
    private final BoredapiClientService boredapiClientService;
    private final ConversionService conversionService;

    public Mono<UserResponse> createUser(CreateUserRequest request) {
        UserCommand userCommand = conversionService.convert(request, UserCommand.class);
        userCommand.setLevel(UserLevel.LEVEL_1);
        return userPersistenceService.save(userCommand)
                .map(item -> conversionService.convert(item, UserResponse.class));
    }

    public Mono<UserResponse> updateUser(long id, UpdateUserRequest request) {
        return userPersistenceService.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(
                        HttpConstants.USER_NOT_FOUND)))
                .map(userCommand -> {
                    if (StringUtils.isNotBlank(request.getCardId())) {
                        userCommand.setCardId(request.getCardId());
                    }
                    if (StringUtils.isNotBlank(request.getFirstName())) {
                        userCommand.setFirstName(request.getFirstName());
                    }
                    if (StringUtils.isNotBlank(request.getSecondName())) {
                        userCommand.setSecondName(request.getSecondName());
                    }
                    if (StringUtils.isNotBlank(request.getType())) {
                        userCommand.setType(UserType.find(request.getType())
                                .orElseThrow(() -> new ServiceException(HttpConstants.FAILED_TO_CONVERT_VALUE_TO_ENUM)));
                    }
                    if (request.getStatus() != null) {
                        userCommand.setStatus(UserStatus.find(request.getStatus())
                                .orElseThrow(() -> new ServiceException(HttpConstants.FAILED_TO_CONVERT_VALUE_TO_ENUM)));
                    }
                    if (StringUtils.isNotBlank(request.getDateOfBirth())) {
                        userCommand.setDateOfBirth(request.getDateOfBirth());
                    }
                    if (request.getAge() != null) {
                        userCommand.setAge(request.getAge());
                    }
                    return userCommand;
                })
                .flatMap(userPersistenceService::save)
                .map(item -> conversionService.convert(item, UserResponse.class));
    }

    public Mono<UserResponse> getUser(long id) {
        return userPersistenceService.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(HttpConstants.USER_NOT_FOUND)))
                .map(item -> conversionService.convert(item, UserResponse.class))
                .flatMap(userResponse -> boredapiClientService.getActivity()
                        .map(activityCommand -> conversionService.convert(activityCommand, ActivityResponse.class))
                        .map(userResponse::setActivity));
    }

    public Mono<CustomPage<UserShortResponse>> getUserList(Pageable pageable) {
        return userPersistenceService.findAll(pageable)
                .map(pageResponse -> pageResponse.map(item -> conversionService.convert(item, UserShortResponse.class)));
    }

    public void deleteUser(long id) {
        userPersistenceService.delete(id);
    }

    public Mono<String> generateRSA(Long id) {
        return userPersistenceService.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(HttpConstants.USER_NOT_FOUND)))
                .publishOn(Schedulers.boundedElastic())
                .flatMap(userCommand -> Mono.fromCallable(CryptoUtils::generateKeyPair))
                .map(keyPair -> Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
    }
}
