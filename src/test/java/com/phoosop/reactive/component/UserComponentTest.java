package com.phoosop.reactive.component;

import com.phoosop.reactive.exception.NotFoundException;
import com.phoosop.reactive.exception.ServiceException;
import com.phoosop.reactive.exception.StatusConstants.HttpConstants;
import com.phoosop.reactive.model.CustomPage;
import com.phoosop.reactive.model.command.ActivityCommand;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserComponentTest {

    @InjectMocks
    private UserComponent userComponent;

    @Mock
    private ConversionService conversionService;

    @Mock
    private UserPersistenceService userPersistenceService;

    @Mock
    private BoredapiClientService boredapiClientService;

    @Test
    public void testCreateUser() {
        CreateUserRequest request = new CreateUserRequest();
        UserCommand returnConverterToUserCommand = new UserCommand();
        UserCommand returnPersistence = new UserCommand();

        UserResponse returnConverterToUserResponse = new UserResponse()
                .setAge(1)
                .setCardId("card_id")
                .setDateOfBirth("27-11-1991")
                .setStatus(UserStatus.ACTIVE.getValue())
                .setType(UserType.USER.getValue())
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setId(1L)
                .setLevel(UserLevel.LEVEL_1.getValue());

        Mockito.when(conversionService.convert(request, UserCommand.class)).thenReturn(returnConverterToUserCommand);
        Mockito.when(userPersistenceService.save(returnConverterToUserCommand)).thenReturn(Mono.just(returnPersistence));
        Mockito.when(conversionService.convert(returnPersistence, UserResponse.class)).thenReturn(returnConverterToUserResponse);
        StepVerifier.create(userComponent.createUser(request))
                .expectNextMatches(result -> result.getAge().equals(1)
                        && result.getCardId().equals("card_id")
                        && result.getDateOfBirth().equals("27-11-1991")
                        && result.getStatus().equals(UserStatus.ACTIVE.getValue())
                        && result.getType().equals(UserType.USER.getValue())
                        && result.getFirstName().equals("firstName")
                        && result.getSecondName().equals("secondName")
                        && result.getId().equals(1L)
                        && result.getLevel().equals(UserLevel.LEVEL_1.getValue()))
                .verifyComplete();
        Mockito.verify(conversionService, Mockito.times(1)).convert(request, UserCommand.class);
        Mockito.verify(userPersistenceService, Mockito.times(1)).save(returnConverterToUserCommand);
        Mockito.verify(conversionService, Mockito.times(1)).convert(returnPersistence, UserResponse.class);
    }

    @Test
    public void testUpdateUser() {
        long id = 1L;
        UpdateUserRequest request = new UpdateUserRequest()
                .setAge(1)
                .setCardId("card_id")
                .setDateOfBirth("27-11-1991")
                .setStatus(UserStatus.ACTIVE.getValue())
                .setType(UserType.USER.getValue())
                .setFirstName("firstName")
                .setSecondName("secondName");

        UserCommand findById = new UserCommand()
                .setId(1L)
                .setLevel(UserLevel.LEVEL_1);

        UserResponse returnConverterToUserResponse = new UserResponse()
                .setAge(1)
                .setCardId("card_id")
                .setDateOfBirth("27-11-1991")
                .setStatus(UserStatus.ACTIVE.getValue())
                .setType(UserType.USER.getValue())
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setId(1L)
                .setLevel(UserLevel.LEVEL_1.getValue());

        Mockito.when(userPersistenceService.findById(id)).thenReturn(Mono.just(findById));
        Mockito.when(conversionService.convert(findById, UserResponse.class)).thenReturn(returnConverterToUserResponse);
        Mockito.when(userPersistenceService.save(findById)).then(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            UserCommand userCommand = (UserCommand) args[0];
            assertThat(userCommand.getAge()).isEqualTo(1);
            assertThat(userCommand.getCardId()).isEqualTo("card_id");
            assertThat(userCommand.getDateOfBirth()).isEqualTo("27-11-1991");
            assertThat(userCommand.getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(userCommand.getType()).isEqualTo(UserType.USER);
            assertThat(userCommand.getFirstName()).isEqualTo("firstName");
            assertThat(userCommand.getSecondName()).isEqualTo("secondName");
            assertThat(userCommand.getId()).isEqualTo(1L);
            assertThat(userCommand.getLevel()).isEqualTo(UserLevel.LEVEL_1);
            return Mono.just(userCommand);
        });
        StepVerifier.create(userComponent.updateUser(id, request))
                .expectNextMatches(result -> result.getAge().equals(1)
                        && result.getCardId().equals("card_id")
                        && result.getDateOfBirth().equals("27-11-1991")
                        && result.getStatus().equals(UserStatus.ACTIVE.getValue())
                        && result.getType().equals(UserType.USER.getValue())
                        && result.getFirstName().equals("firstName")
                        && result.getSecondName().equals("secondName")
                        && result.getId().equals(1L)
                        && result.getLevel().equals(UserLevel.LEVEL_1.getValue()))
                .verifyComplete();
        Mockito.verify(userPersistenceService, Mockito.times(1)).findById(id);
        Mockito.verify(userPersistenceService, Mockito.times(1)).save(Mockito.any(UserCommand.class));
        Mockito.verify(conversionService, Mockito.times(1)).convert(findById, UserResponse.class);

    }

    @Test
    public void testUpdateUser_userNotFound() {
        long id = 1L;
        UpdateUserRequest request = new UpdateUserRequest()
                .setAge(1)
                .setCardId("card_id")
                .setDateOfBirth("27-11-1991")
                .setStatus(UserStatus.ACTIVE.getValue())
                .setType(UserType.USER.getValue())
                .setFirstName("firstName")
                .setSecondName("secondName");
        Mockito.when(userPersistenceService.findById(id)).thenReturn(Mono.empty());
        StepVerifier.create(userComponent.updateUser(id, request))
                .expectErrorMatches(throwable -> {
                    if (throwable instanceof NotFoundException) {
                        NotFoundException exception = (NotFoundException) throwable;
                        return exception.getStatus().equals(HttpConstants.USER_NOT_FOUND);
                    }
                    return false;
                }).verify();
        Mockito.verify(userPersistenceService, Mockito.times(1)).findById(id);

    }

    @Test
    public void testUpdateUser_wrongType() {
        long id = 1L;
        UpdateUserRequest request = new UpdateUserRequest()
                .setAge(1)
                .setCardId("card_id")
                .setDateOfBirth("27-11-1991")
                .setStatus(UserStatus.ACTIVE.getValue())
                .setType("321321")
                .setFirstName("firstName")
                .setSecondName("secondName");
        UserCommand findById = new UserCommand()
                .setId(1L)
                .setLevel(UserLevel.LEVEL_1);
        Mockito.when(userPersistenceService.findById(id)).thenReturn(Mono.just(findById));
        StepVerifier.create(userComponent.updateUser(id, request))
                .expectErrorMatches(throwable -> {
                    if (throwable instanceof ServiceException) {
                        ServiceException exception = (ServiceException) throwable;
                        return exception.getStatus().equals(HttpConstants.FAILED_TO_CONVERT_VALUE_TO_ENUM);
                    }
                    return false;
                }).verify();
        Mockito.verify(userPersistenceService, Mockito.times(1)).findById(id);

    }

    @Test
    public void testUpdateUser_wrongStatus() {
        long id = 1L;
        UpdateUserRequest request = new UpdateUserRequest()
                .setAge(1)
                .setCardId("card_id")
                .setDateOfBirth("27-11-1991")
                .setStatus(321)
                .setType(UserType.USER.getValue())
                .setFirstName("firstName")
                .setSecondName("secondName");
        UserCommand findById = new UserCommand()
                .setId(1L)
                .setLevel(UserLevel.LEVEL_1);
        Mockito.when(userPersistenceService.findById(id)).thenReturn(Mono.just(findById));
        StepVerifier.create(userComponent.updateUser(id, request))
                .expectErrorMatches(throwable -> {
                    if (throwable instanceof ServiceException) {
                        ServiceException exception = (ServiceException) throwable;
                        return exception.getStatus().equals(HttpConstants.FAILED_TO_CONVERT_VALUE_TO_ENUM);
                    }
                    return false;
                }).verify();
        Mockito.verify(userPersistenceService, Mockito.times(1)).findById(id);

    }

    @Test
    public void testGetUser() {
        long id = 1L;
        UserCommand findById = new UserCommand()
                .setId(1L)
                .setAge(1)
                .setCardId("card_id")
                .setDateOfBirth("27-11-1991")
                .setStatus(UserStatus.ACTIVE)
                .setType(UserType.USER)
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setLevel(UserLevel.LEVEL_1);

        UserResponse returnConverterToUserResponse = new UserResponse()
                .setAge(1)
                .setCardId("card_id")
                .setDateOfBirth("27-11-1991")
                .setStatus(UserStatus.ACTIVE.getValue())
                .setType(UserType.USER.getValue())
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setId(1L)
                .setLevel(UserLevel.LEVEL_1.getValue());

        ActivityCommand activityCommand = new ActivityCommand()
                .setActivity("activity")
                .setAccessibility(1D)
                .setKey("key")
                .setLink("link")
                .setPrice(100)
                .setType("type")
                .setParticipants(3);

        ActivityResponse activityResponse = new ActivityResponse()
                .setActivity("activity")
                .setAccessibility(1D)
                .setKey("key")
                .setLink("link")
                .setPrice(100)
                .setType("type")
                .setParticipants(3);

        Mockito.when(userPersistenceService.findById(id)).thenReturn(Mono.just(findById));
        Mockito.when(conversionService.convert(findById, UserResponse.class)).thenReturn(returnConverterToUserResponse);
        Mockito.when(boredapiClientService.getActivity()).thenReturn(Mono.just(activityCommand));
        Mockito.when(conversionService.convert(activityCommand, ActivityResponse.class)).thenReturn(activityResponse);

        StepVerifier.create(userComponent.getUser(id))
                .expectNextMatches(result -> result.getAge().equals(1)
                        && result.getCardId().equals("card_id")
                        && result.getDateOfBirth().equals("27-11-1991")
                        && result.getStatus().equals(UserStatus.ACTIVE.getValue())
                        && result.getType().equals(UserType.USER.getValue())
                        && result.getFirstName().equals("firstName")
                        && result.getSecondName().equals("secondName")
                        && result.getId().equals(1L)
                        && result.getLevel().equals(UserLevel.LEVEL_1.getValue())
                        && result.getActivity().getActivity().equals("activity")
                        && result.getActivity().getAccessibility().equals(1D)
                        && result.getActivity().getKey().equals("key")
                        && result.getActivity().getLink().equals("link")
                        && result.getActivity().getPrice().equals(100)
                        && result.getActivity().getType().equals("type")
                        && result.getActivity().getParticipants().equals(3))
                        .verifyComplete();
        Mockito.verify(userPersistenceService, Mockito.times(1)).findById(id);

    }

    @Test
    public void testGetUser_userNotFound() {
        long id = 1L;
        Mockito.when(userPersistenceService.findById(id)).thenReturn(Mono.empty());
        StepVerifier.create(userComponent.getUser(id))
                .expectErrorMatches(throwable -> {
                    if (throwable instanceof NotFoundException) {
                        NotFoundException exception = (NotFoundException) throwable;
                        return exception.getStatus().equals(HttpConstants.USER_NOT_FOUND);
                    }
                    return false;
                }).verify();
        Mockito.verify(userPersistenceService, Mockito.times(1)).findById(id);

    }

    @Test
    public void testGetUserList() {
        PageRequest pageRequest = PageRequest.of(1, 10);
        UserCommand userCommand = new UserCommand()
                .setId(1L)
                .setAge(1)
                .setCardId("card_id")
                .setDateOfBirth("27-11-1991")
                .setStatus(UserStatus.ACTIVE)
                .setType(UserType.USER)
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setLevel(UserLevel.LEVEL_1);

        CustomPage<UserCommand> customPage = new CustomPage<UserCommand>()
                .setContent(Collections.singletonList(userCommand))
                .setSize(1)
                .setNumber(1)
                .setTotalElements(1);

        UserShortResponse returnConverterToUserShortResponse = new UserShortResponse()
                .setId(1L)
                .setCardId("card_id")
                .setStatus(UserStatus.ACTIVE.getValue())
                .setType(UserType.USER.getValue())
                .setFirstName("firstName")
                .setSecondName("secondName");

        Mockito.when(userPersistenceService.findAll(pageRequest)).thenReturn(Mono.just(customPage));
        Mockito.when(conversionService.convert(userCommand, UserShortResponse.class)).thenReturn(returnConverterToUserShortResponse);

        StepVerifier.create(userComponent.getUserList(pageRequest))
                .expectNextMatches(userShortResponseCustomPage -> {
                    UserShortResponse userShortResponse1 = userShortResponseCustomPage.getContent().get(0);

                    return userShortResponse1.getCardId().equals("card_id")
                            && userShortResponse1.getStatus().equals(UserStatus.ACTIVE.getValue())
                            && userShortResponse1.getType().equals(UserType.USER.getValue())
                            && userShortResponse1.getFirstName().equals("firstName")
                            && userShortResponse1.getSecondName().equals("secondName")
                            && userShortResponse1.getId().equals(1L)
                            && userShortResponseCustomPage.getContent().size() == 1
                            && userShortResponseCustomPage.getSize() == 1
                            && userShortResponseCustomPage.getTotalElements() == 1;
                }).verifyComplete();
        Mockito.verify(userPersistenceService, Mockito.times(1)).findAll(pageRequest);

    }

    @Test
    public void deleteUser() {
        long id = 1L;
        userComponent.deleteUser(id);
        Mockito.verify(userPersistenceService, Mockito.times(1)).delete(id);

    }

}