package com.phoosop.reactive.controller;

import com.phoosop.reactive.component.UserComponent;
import com.phoosop.reactive.exception.NotFoundException;
import com.phoosop.reactive.exception.ServiceException;
import com.phoosop.reactive.model.CustomPage;
import com.phoosop.reactive.model.Response;
import com.phoosop.reactive.model.command.UserCommand;
import com.phoosop.reactive.model.enums.UserLevel;
import com.phoosop.reactive.model.enums.UserStatus;
import com.phoosop.reactive.model.enums.UserType;
import com.phoosop.reactive.model.request.CreateUserRequest;
import com.phoosop.reactive.model.request.UpdateUserRequest;
import com.phoosop.reactive.model.response.UserResponse;
import com.phoosop.reactive.model.response.UserShortResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static com.phoosop.reactive.exception.StatusConstants.HttpConstants;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserComponent userComponent;

    @Autowired
    private ConversionService conversionService;

    @Test
    @DisplayName("Create user - success")
    public void testCreateUser_success() throws Exception {
        CreateUserRequest actualRequest = new CreateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER.getValue())
                .setStatus(UserStatus.ACTIVE.getValue())
                .setDateOfBirth("11-11-1991")
                .setAge(18)
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");

        UserCommand userCommand = new UserCommand()
                .setId(1L)
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER)
                .setStatus(UserStatus.ACTIVE)
                .setLevel(UserLevel.LEVEL_1)
                .setAge(18);

        Mockito.when(userComponent.createUser(Mockito.any(CreateUserRequest.class)))
                .thenReturn(Mono.just(userCommand));

        ParameterizedTypeReference<Response<UserResponse>> typeReference = new ParameterizedTypeReference<Response<UserResponse>>() {
        };
        Response<UserResponse> actualResponse = webTestClient.post().uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().isOk()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.times(1)).createUser(Mockito.any(CreateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.SUCCESS.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.SUCCESS.getDesc());
        Assertions.assertThat(actualResponse.getData().getId()).isEqualTo(1L);
        Assertions.assertThat(actualResponse.getData().getCardId()).isEqualTo("cardId");
        Assertions.assertThat(actualResponse.getData().getFirstName()).isEqualTo("firstName");
        Assertions.assertThat(actualResponse.getData().getSecondName()).isEqualTo("secondName");
        Assertions.assertThat(actualResponse.getData().getType()).isEqualTo(UserType.USER.getValue());
        Assertions.assertThat(actualResponse.getData().getStatus()).isEqualTo(UserStatus.ACTIVE.getValue());
        Assertions.assertThat(actualResponse.getData().getLevel()).isEqualTo(UserLevel.LEVEL_1.getValue());
        Assertions.assertThat(actualResponse.getData().getAge()).isEqualTo(18);
    }

    @Test
    @DisplayName("Create user - ServiceException")
    public void testCreateUser_ServiceException() throws Exception {
        CreateUserRequest actualRequest = new CreateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER.getValue())
                .setStatus(UserStatus.ACTIVE.getValue())
                .setDateOfBirth("11-11-1991")
                .setAge(18)
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");

        Mockito.when(userComponent.createUser(Mockito.any(CreateUserRequest.class)))
                .thenThrow(new ServiceException(HttpConstants.FAILED_TO_CONVERT_VALUE_TO_ENUM));

        ParameterizedTypeReference<Response> typeReference = new ParameterizedTypeReference<Response>() {
        };

        Response actualResponse = webTestClient.post().uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.times(1)).createUser(Mockito.any(CreateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.FAILED_TO_CONVERT_VALUE_TO_ENUM.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.FAILED_TO_CONVERT_VALUE_TO_ENUM.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();

    }

    @Test
    @DisplayName("Create user - Exception")
    public void testCreateUser_Exception() throws Exception {
        CreateUserRequest actualRequest = new CreateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER.getValue())
                .setStatus(UserStatus.ACTIVE.getValue())
                .setDateOfBirth("11-11-1991")
                .setAge(18)
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");

        Mockito.doThrow(new RuntimeException()).when(userComponent).createUser(Mockito.any(CreateUserRequest.class));

        ParameterizedTypeReference<Response> typeReference = new ParameterizedTypeReference<Response>() {
        };

        Response actualResponse = webTestClient.post().uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.times(1)).createUser(Mockito.any(CreateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.INTERNAL_SERVER_ERROR.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.INTERNAL_SERVER_ERROR.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();

    }

    @Test
    @DisplayName("Create user - card_id is required")
    public void testCreateUser_cardIdIsRequired() throws Exception {
        CreateUserRequest actualRequest = new CreateUserRequest()
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER.getValue())
                .setStatus(UserStatus.ACTIVE.getValue())
                .setDateOfBirth("11-11-1991")
                .setAge(18)
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");

        ParameterizedTypeReference<Response> typeReference = new ParameterizedTypeReference<Response>() {
        };

        Response actualResponse = webTestClient.post().uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.never()).createUser(Mockito.any(CreateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.CARD_ID_IS_REQUIRED.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.CARD_ID_IS_REQUIRED.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();

    }

    @Test
    @DisplayName("Create user - first_name is required")
    public void testCreateUser_firstNameIsRequired() throws Exception {
        CreateUserRequest actualRequest = new CreateUserRequest()
                .setCardId("cardId")
                .setSecondName("secondName")
                .setType(UserType.USER.getValue())
                .setStatus(UserStatus.ACTIVE.getValue())
                .setDateOfBirth("11-11-1991")
                .setAge(18)
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");

        ParameterizedTypeReference<Response> typeReference = new ParameterizedTypeReference<Response>() {
        };

        Response actualResponse = webTestClient.post().uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.never()).createUser(Mockito.any(CreateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.FIRST_NAME_IS_REQUIRED.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.FIRST_NAME_IS_REQUIRED.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();

    }

    @Test
    @DisplayName("Create user - second_name is required")
    public void testCreateUser_secondNameIsRequired() throws Exception {
        CreateUserRequest actualRequest = new CreateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setType(UserType.USER.getValue())
                .setStatus(UserStatus.ACTIVE.getValue())
                .setDateOfBirth("11-11-1991")
                .setAge(18)
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");

        ParameterizedTypeReference<Response> typeReference = new ParameterizedTypeReference<Response>() {
        };

        Response actualResponse = webTestClient.post().uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.never()).createUser(Mockito.any(CreateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.SECOND_NAME_IS_REQUIRED.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.SECOND_NAME_IS_REQUIRED.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();

    }

    @Test
    @DisplayName("Create user - type is required")
    public void testCreateUser_typeIsRequired() throws Exception {
        CreateUserRequest actualRequest = new CreateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setStatus(UserStatus.ACTIVE.getValue())
                .setDateOfBirth("11-11-1991")
                .setAge(18)
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");

        ParameterizedTypeReference<Response> typeReference = new ParameterizedTypeReference<Response>() {
        };

        Response actualResponse = webTestClient.post().uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.never()).createUser(Mockito.any(CreateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.TYPE_IS_REQUIRED.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.TYPE_IS_REQUIRED.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();

    }

    @Test
    @DisplayName("Create user - status is required")
    public void testCreateUser_statusIsRequired() throws Exception {
        CreateUserRequest actualRequest = new CreateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER.getValue())
                .setDateOfBirth("11-11-1991")
                .setAge(18)
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");

        ParameterizedTypeReference<Response> typeReference = new ParameterizedTypeReference<Response>() {
        };

        Response actualResponse = webTestClient.post().uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.never()).createUser(Mockito.any(CreateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.STATUS_IS_REQUIRED.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.STATUS_IS_REQUIRED.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();

    }

    @Test
    @DisplayName("Create user - date_of_birth is required")
    public void testCreateUser_dateOfBirthIsRequired() throws Exception {
        CreateUserRequest actualRequest = new CreateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER.getValue())
                .setStatus(UserStatus.ACTIVE.getValue())
                .setAge(18)
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");

        ParameterizedTypeReference<Response> typeReference = new ParameterizedTypeReference<Response>() {
        };

        Response actualResponse = webTestClient.post().uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.never()).createUser(Mockito.any(CreateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.DATE_OF_BIRTH_IS_REQUIRED.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.DATE_OF_BIRTH_IS_REQUIRED.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();

    }

    @Test
    @DisplayName("Create user - age is required")
    public void testCreateUser_ageIsRequired() throws Exception {
        CreateUserRequest actualRequest = new CreateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER.getValue())
                .setStatus(UserStatus.ACTIVE.getValue())
                .setDateOfBirth("11-11-1991")
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");

        ParameterizedTypeReference<Response> typeReference = new ParameterizedTypeReference<Response>() {
        };

        Response actualResponse = webTestClient.post().uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.never()).createUser(Mockito.any(CreateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.AGE_IS_REQUIRED.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.AGE_IS_REQUIRED.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();

    }

    @Test
    @DisplayName("Create user - mobile_number is required")
    public void testCreateUser_mobileNumberIsRequired() throws Exception {
        CreateUserRequest actualRequest = new CreateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER.getValue())
                .setStatus(UserStatus.ACTIVE.getValue())
                .setDateOfBirth("11-11-1991")
                .setAge(18)
                .setMobileBrand("Apple");

        ParameterizedTypeReference<Response> typeReference = new ParameterizedTypeReference<Response>() {
        };

        Response actualResponse = webTestClient.post().uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.never()).createUser(Mockito.any(CreateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.MOBILE_NUMBER_IS_REQUIRED.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.MOBILE_NUMBER_IS_REQUIRED.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();

    }

    @Test
    @DisplayName("Create user - mobile_brand is required")
    public void testCreateUser_mobileBrandIsRequired() throws Exception {
        CreateUserRequest actualRequest = new CreateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER.getValue())
                .setStatus(UserStatus.ACTIVE.getValue())
                .setDateOfBirth("11-11-1991")
                .setAge(18)
                .setMobileNumber("12345678901");

        ParameterizedTypeReference<Response> typeReference = new ParameterizedTypeReference<Response>() {
        };

        Response actualResponse = webTestClient.post().uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.never()).createUser(Mockito.any(CreateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.MOBILE_BRAND_IS_REQUIRED.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.MOBILE_BRAND_IS_REQUIRED.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();

    }

    @Test
    @DisplayName("Create user - type is invalid")
    public void testCreateUser_typeIsInvalid() throws Exception {
        CreateUserRequest actualRequest = new CreateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType("wrong")
                .setStatus(UserStatus.ACTIVE.getValue())
                .setDateOfBirth("11-11-1991")
                .setAge(18)
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");

        ParameterizedTypeReference<Response> typeReference = new ParameterizedTypeReference<Response>() {
        };

        Response actualResponse = webTestClient.post().uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.never()).createUser(Mockito.any(CreateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.TYPE_IS_INVALID.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.TYPE_IS_INVALID.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();

    }

    @Test
    @DisplayName("Create user - status is invalid")
    public void testCreateUser_statusIsInvalid() throws Exception {
        CreateUserRequest actualRequest = new CreateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER.getValue())
                .setStatus(99)
                .setDateOfBirth("11-11-1991")
                .setAge(18)
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");

        ParameterizedTypeReference<Response> typeReference = new ParameterizedTypeReference<Response>() {
        };

        Response actualResponse = webTestClient.post().uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.never()).createUser(Mockito.any(CreateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.STATUS_IS_INVALID.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.STATUS_IS_INVALID.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();

    }

    @Test
    @DisplayName("Create user - date_of_birth is invalid")
    public void testCreateUser_dateOfBirthIsInvalid() throws Exception {
        CreateUserRequest actualRequest = new CreateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER.getValue())
                .setStatus(UserStatus.ACTIVE.getValue())
                .setDateOfBirth("11/11/1991")
                .setAge(18)
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");

        ParameterizedTypeReference<Response> typeReference = new ParameterizedTypeReference<Response>() {
        };

        Response actualResponse = webTestClient.post().uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.never()).createUser(Mockito.any(CreateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.DATE_OF_BIRTH_IS_INVALID.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.DATE_OF_BIRTH_IS_INVALID.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();

    }

    @Test
    @DisplayName("Create user - age is invalid (MIN)")
    public void testCreateUser_ageIsInvalidMin() throws Exception {
        CreateUserRequest actualRequest = new CreateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER.getValue())
                .setStatus(UserStatus.ACTIVE.getValue())
                .setDateOfBirth("11-11-1991")
                .setAge(14)
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");

        ParameterizedTypeReference<Response> typeReference = new ParameterizedTypeReference<Response>() {
        };

        Response actualResponse = webTestClient.post().uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.never()).createUser(Mockito.any(CreateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.AGE_IS_INVALID.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.AGE_IS_INVALID.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();

    }

    @Test
    @DisplayName("Create user - age is invalid (MAX)")
    public void testCreateUser_ageIsInvalidMax() throws Exception {
        CreateUserRequest actualRequest = new CreateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER.getValue())
                .setStatus(UserStatus.ACTIVE.getValue())
                .setDateOfBirth("11-11-1991")
                .setAge(99)
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");

        ParameterizedTypeReference<Response> typeReference = new ParameterizedTypeReference<Response>() {
        };

        Response actualResponse = webTestClient.post().uri("/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.never()).createUser(Mockito.any(CreateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.AGE_IS_INVALID.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.AGE_IS_INVALID.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();

    }

    @Test
    @DisplayName("Update user - success")
    public void testUpdateUser_success() throws Exception {
        UpdateUserRequest actualRequest = new UpdateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER.getValue())
                .setStatus(UserStatus.ACTIVE.getValue())
                .setDateOfBirth("11-11-1991")
                .setAge(18)
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");

        UserCommand userCommand = new UserCommand()
                .setId(1L)
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER)
                .setStatus(UserStatus.ACTIVE)
                .setLevel(UserLevel.LEVEL_1)
                .setAge(18);

        Mockito.when(userComponent.updateUser(Mockito.eq(1L), Mockito.any(UpdateUserRequest.class)))
                .thenReturn(Mono.just(userCommand));

        ParameterizedTypeReference<Response<UserResponse>> typeReference = new ParameterizedTypeReference<Response<UserResponse>>() {
        };

        Response<UserResponse> actualResponse = webTestClient.patch().uri("/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().isOk()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.times(1)).updateUser(Mockito.eq(1L), Mockito.any(UpdateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.SUCCESS.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.SUCCESS.getDesc());

        Assertions.assertThat(actualResponse.getData().getId()).isEqualTo(1L);
        Assertions.assertThat(actualResponse.getData().getCardId()).isEqualTo("cardId");
        Assertions.assertThat(actualResponse.getData().getFirstName()).isEqualTo("firstName");
        Assertions.assertThat(actualResponse.getData().getSecondName()).isEqualTo("secondName");
        Assertions.assertThat(actualResponse.getData().getType()).isEqualTo(UserType.USER.getValue());
        Assertions.assertThat(actualResponse.getData().getStatus()).isEqualTo(UserStatus.ACTIVE.getValue());
        Assertions.assertThat(actualResponse.getData().getLevel()).isEqualTo(UserLevel.LEVEL_1.getValue());
        Assertions.assertThat(actualResponse.getData().getAge()).isEqualTo(18);

    }

    @Test
    @DisplayName("Update user - ServiceException")
    public void testUpdateUser_ServiceException() throws Exception {
        UpdateUserRequest actualRequest = new UpdateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER.getValue())
                .setStatus(UserStatus.ACTIVE.getValue())
                .setDateOfBirth("11-11-1991")
                .setAge(18)
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");

        Mockito.when(userComponent.updateUser(Mockito.eq(1L), Mockito.any(UpdateUserRequest.class)))
                .thenThrow(new ServiceException(HttpConstants.FAILED_TO_CONVERT_VALUE_TO_ENUM));

        ParameterizedTypeReference<Response<UserResponse>> typeReference = new ParameterizedTypeReference<Response<UserResponse>>() {
        };

        Response<UserResponse> actualResponse = webTestClient.patch().uri("/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.times(1)).updateUser(Mockito.eq(1L), Mockito.any(UpdateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.FAILED_TO_CONVERT_VALUE_TO_ENUM.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.FAILED_TO_CONVERT_VALUE_TO_ENUM.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();

    }

    @Test
    @DisplayName("Update user - NotFountException")
    public void testUpdateUser_NotFountException() throws Exception {
        UpdateUserRequest actualRequest = new UpdateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER.getValue())
                .setStatus(UserStatus.ACTIVE.getValue())
                .setDateOfBirth("11-11-1991")
                .setAge(18)
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");

        Mockito.when(userComponent.updateUser(Mockito.eq(1L), Mockito.any(UpdateUserRequest.class)))
                .thenThrow(new NotFoundException(HttpConstants.USER_NOT_FOUND));

        ParameterizedTypeReference<Response<UserResponse>> typeReference = new ParameterizedTypeReference<Response<UserResponse>>() {
        };

        Response<UserResponse> actualResponse = webTestClient.patch().uri("/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.times(1)).updateUser(Mockito.eq(1L), Mockito.any(UpdateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.USER_NOT_FOUND.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.USER_NOT_FOUND.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();

    }

    @Test
    @DisplayName("Update user - Exception")
    public void testUpdateUser_Exception() throws Exception {
        UpdateUserRequest actualRequest = new UpdateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER.getValue())
                .setStatus(UserStatus.ACTIVE.getValue())
                .setDateOfBirth("11-11-1991")
                .setAge(18)
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");

        Mockito.when(userComponent.updateUser(Mockito.eq(1L), Mockito.any(UpdateUserRequest.class)))
                .thenThrow(new RuntimeException());

        ParameterizedTypeReference<Response<UserResponse>> typeReference = new ParameterizedTypeReference<Response<UserResponse>>() {
        };

        Response<UserResponse> actualResponse = webTestClient.patch().uri("/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.times(1)).updateUser(Mockito.eq(1L), Mockito.any(UpdateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.INTERNAL_SERVER_ERROR.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.INTERNAL_SERVER_ERROR.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();

    }

    @Test
    @DisplayName("Update user - type is invalid")
    public void testUpdateUser_typeIsInvalid() throws Exception {
        UpdateUserRequest actualRequest = new UpdateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType("wrong")
                .setStatus(UserStatus.ACTIVE.getValue())
                .setDateOfBirth("11-11-1991")
                .setAge(18)
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");

        ParameterizedTypeReference<Response<UserResponse>> typeReference = new ParameterizedTypeReference<Response<UserResponse>>() {
        };

        Response<UserResponse> actualResponse = webTestClient.patch().uri("/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.never()).updateUser(Mockito.anyLong(), Mockito.any(UpdateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.TYPE_IS_INVALID.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.TYPE_IS_INVALID.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();

    }

    @Test
    @DisplayName("Update user - status is invalid")
    public void testUpdateUser_statusIsInvalid() throws Exception {
        UpdateUserRequest actualRequest = new UpdateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER.getValue())
                .setStatus(99)
                .setDateOfBirth("11-11-1991")
                .setAge(18)
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");

        ParameterizedTypeReference<Response<UserResponse>> typeReference = new ParameterizedTypeReference<Response<UserResponse>>() {
        };

        Response<UserResponse> actualResponse = webTestClient.patch().uri("/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.never()).updateUser(Mockito.anyLong(), Mockito.any(UpdateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.STATUS_IS_INVALID.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.STATUS_IS_INVALID.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();

    }

    @Test
    @DisplayName("Update user - date_of_birth is invalid")
    public void testUpdateUser_dateOfBirthIsInvalid() throws Exception {
        UpdateUserRequest actualRequest = new UpdateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER.getValue())
                .setStatus(UserStatus.ACTIVE.getValue())
                .setDateOfBirth("11/11/1991")
                .setAge(18)
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");

        ParameterizedTypeReference<Response<UserResponse>> typeReference = new ParameterizedTypeReference<Response<UserResponse>>() {
        };

        Response<UserResponse> actualResponse = webTestClient.patch().uri("/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.never()).updateUser(Mockito.anyLong(), Mockito.any(UpdateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.DATE_OF_BIRTH_IS_INVALID.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.DATE_OF_BIRTH_IS_INVALID.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();

    }

    @Test
    @DisplayName("Update user - age is invalid (MIN)")
    public void testUpdateUser_ageIsInvalidMin() throws Exception {
        UpdateUserRequest actualRequest = new UpdateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER.getValue())
                .setStatus(UserStatus.ACTIVE.getValue())
                .setDateOfBirth("11-11-1991")
                .setAge(14)
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");

        ParameterizedTypeReference<Response<UserResponse>> typeReference = new ParameterizedTypeReference<Response<UserResponse>>() {
        };

        Response<UserResponse> actualResponse = webTestClient.patch().uri("/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.never()).updateUser(Mockito.anyLong(), Mockito.any(UpdateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.AGE_IS_INVALID.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.AGE_IS_INVALID.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();

    }

    @Test
    @DisplayName("Update user - age is invalid (MAX)")
    public void testUpdateUser_ageIsInvalidMax() throws Exception {
        UpdateUserRequest actualRequest = new UpdateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER.getValue())
                .setStatus(UserStatus.ACTIVE.getValue())
                .setDateOfBirth("11-11-1991")
                .setAge(99)
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");

        ParameterizedTypeReference<Response<UserResponse>> typeReference = new ParameterizedTypeReference<Response<UserResponse>>() {
        };

        Response<UserResponse> actualResponse = webTestClient.patch().uri("/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(actualRequest))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.never()).updateUser(Mockito.anyLong(), Mockito.any(UpdateUserRequest.class));

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.AGE_IS_INVALID.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.AGE_IS_INVALID.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();

    }

    @Test
    @DisplayName("Get user - success")
    public void testGetUser_success() throws Exception {
        UserCommand userCommand = new UserCommand()
                .setId(1L)
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER)
                .setStatus(UserStatus.ACTIVE)
                .setLevel(UserLevel.LEVEL_1)
                .setAge(18);

        Mockito.when(userComponent.getUser(Mockito.eq(1L)))
                .thenReturn(Mono.just(userCommand));

        ParameterizedTypeReference<Response<UserResponse>> typeReference = new ParameterizedTypeReference<Response<UserResponse>>() {
        };

        Response<UserResponse> actualResponse = webTestClient.get().uri("/v1/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.times(1)).getUser(Mockito.anyLong());

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.SUCCESS.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.SUCCESS.getDesc());

        Assertions.assertThat(actualResponse.getData().getId()).isEqualTo(1L);
        Assertions.assertThat(actualResponse.getData().getCardId()).isEqualTo("cardId");
        Assertions.assertThat(actualResponse.getData().getFirstName()).isEqualTo("firstName");
        Assertions.assertThat(actualResponse.getData().getSecondName()).isEqualTo("secondName");
        Assertions.assertThat(actualResponse.getData().getType()).isEqualTo(UserType.USER.getValue());
        Assertions.assertThat(actualResponse.getData().getStatus()).isEqualTo(UserStatus.ACTIVE.getValue());
        Assertions.assertThat(actualResponse.getData().getLevel()).isEqualTo(UserLevel.LEVEL_1.getValue());
        Assertions.assertThat(actualResponse.getData().getAge()).isEqualTo(18);
    }

    @Test
    @DisplayName("Get user - NotFoundException")
    public void testGetUser_NotFoundException() throws Exception {

        Mockito.when(userComponent.getUser(Mockito.eq(1L)))
                .thenThrow(new NotFoundException(HttpConstants.USER_NOT_FOUND));

        ParameterizedTypeReference<Response<UserResponse>> typeReference = new ParameterizedTypeReference<Response<UserResponse>>() {
        };

        Response<UserResponse> actualResponse = webTestClient.get().uri("/v1/users/1")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.times(1)).getUser(Mockito.anyLong());

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.USER_NOT_FOUND.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.USER_NOT_FOUND.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();
    }

    @Test
    @DisplayName("Get user - ServiceException")
    public void testGetUser_ServiceException() throws Exception {
        Mockito.when(userComponent.getUser(Mockito.eq(1L)))
                .thenThrow(new ServiceException(HttpConstants.FAILED_TO_CONVERT_VALUE_TO_ENUM));

        ParameterizedTypeReference<Response<UserResponse>> typeReference = new ParameterizedTypeReference<Response<UserResponse>>() {
        };

        Response<UserResponse> actualResponse = webTestClient.get().uri("/v1/users/1")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.times(1)).getUser(Mockito.anyLong());

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.FAILED_TO_CONVERT_VALUE_TO_ENUM.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.FAILED_TO_CONVERT_VALUE_TO_ENUM.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();
    }

    @Test
    @DisplayName("Get user - Exception")
    public void testGetUser_Exception() throws Exception {
        Mockito.when(userComponent.getUser(Mockito.eq(1L)))
                .thenThrow(new RuntimeException());

        ParameterizedTypeReference<Response<UserResponse>> typeReference = new ParameterizedTypeReference<Response<UserResponse>>() {
        };

        Response<UserResponse> actualResponse = webTestClient.get().uri("/v1/users/1")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.times(1)).getUser(Mockito.anyLong());

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.INTERNAL_SERVER_ERROR.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.INTERNAL_SERVER_ERROR.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();
    }

    @Test
    @DisplayName("Get user list - success")
    public void testGetUserList_success() throws Exception {
        UserCommand userCommand1 = new UserCommand()
                .setId(1L)
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER)
                .setStatus(UserStatus.ACTIVE)
                .setLevel(UserLevel.LEVEL_1)
                .setAge(18);

        UserCommand userCommand2 = new UserCommand()
                .setId(2L)
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER)
                .setStatus(UserStatus.ACTIVE)
                .setLevel(UserLevel.LEVEL_1)
                .setAge(18);

        Mockito.when(userComponent.getUserList(Mockito.any()))
                .thenReturn(Mono.just(new CustomPage<>(Arrays.asList(userCommand1, userCommand2), 1, 2, 2)));

        ParameterizedTypeReference<Response<CustomPage<UserShortResponse>>> typeReference = new ParameterizedTypeReference<Response<CustomPage<UserShortResponse>>>() {
        };

        Response<CustomPage<UserShortResponse>> actualResponse = webTestClient.get().uri("/v1/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.times(1)).getUserList(Mockito.any());

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.SUCCESS.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.SUCCESS.getDesc());

        Assertions.assertThat(actualResponse.getData().getNumber()).isEqualTo(1);
        Assertions.assertThat(actualResponse.getData().getSize()).isEqualTo(2);
        Assertions.assertThat(actualResponse.getData().getTotalElements()).isEqualTo(2);

        Assertions.assertThat(actualResponse.getData().getContent().size()).isEqualTo(2);
        Assertions.assertThat(actualResponse.getData().getContent().get(0).getId()).isEqualTo(1L);
        Assertions.assertThat(actualResponse.getData().getContent().get(0).getCardId()).isEqualTo("cardId");
        Assertions.assertThat(actualResponse.getData().getContent().get(0).getFirstName()).isEqualTo("firstName");
        Assertions.assertThat(actualResponse.getData().getContent().get(0).getSecondName()).isEqualTo("secondName");
        Assertions.assertThat(actualResponse.getData().getContent().get(0).getType()).isEqualTo(UserType.USER.getValue());
        Assertions.assertThat(actualResponse.getData().getContent().get(0).getStatus()).isEqualTo(UserStatus.ACTIVE.getValue());
        Assertions.assertThat(actualResponse.getData().getContent().get(1).getId()).isEqualTo(2L);
        Assertions.assertThat(actualResponse.getData().getContent().get(1).getCardId()).isEqualTo("cardId");
        Assertions.assertThat(actualResponse.getData().getContent().get(1).getFirstName()).isEqualTo("firstName");
        Assertions.assertThat(actualResponse.getData().getContent().get(1).getSecondName()).isEqualTo("secondName");
        Assertions.assertThat(actualResponse.getData().getContent().get(1).getType()).isEqualTo(UserType.USER.getValue());
        Assertions.assertThat(actualResponse.getData().getContent().get(1).getStatus()).isEqualTo(UserStatus.ACTIVE.getValue());
    }

    @Test
    @DisplayName("Get user list - ServiceException")
    public void testGetUserList_ServiceException() throws Exception {

        Mockito.when(userComponent.getUserList(Mockito.any()))
                .thenThrow(new ServiceException(HttpConstants.FAILED_TO_CONVERT_VALUE_TO_ENUM));

        ParameterizedTypeReference<Response<CustomPage<UserShortResponse>>> typeReference = new ParameterizedTypeReference<Response<CustomPage<UserShortResponse>>>() {
        };

        Response<CustomPage<UserShortResponse>> actualResponse = webTestClient.get().uri("/v1/users")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.times(1)).getUserList(Mockito.any());

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.FAILED_TO_CONVERT_VALUE_TO_ENUM.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.FAILED_TO_CONVERT_VALUE_TO_ENUM.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();
    }

    @Test
    @DisplayName("Get user list - Exception")
    public void testGetUserList_Exception() throws Exception {

        Mockito.when(userComponent.getUserList(Mockito.any()))
                .thenThrow(new RuntimeException());

        ParameterizedTypeReference<Response<CustomPage<UserShortResponse>>> typeReference = new ParameterizedTypeReference<Response<CustomPage<UserShortResponse>>>() {
        };

        Response<CustomPage<UserShortResponse>> actualResponse = webTestClient.get().uri("/v1/users")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.times(1)).getUserList(Mockito.any());

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.INTERNAL_SERVER_ERROR.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.INTERNAL_SERVER_ERROR.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();
    }

    @Test
    @DisplayName("Delete user - success")
    public void testDeleteUser_success() throws Exception {

        ParameterizedTypeReference<Response<Void>> typeReference = new ParameterizedTypeReference<Response<Void>>() {
        };

        Response<Void> actualResponse = webTestClient.delete().uri("/v1/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.times(1)).deleteUser(1L);

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.SUCCESS.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.SUCCESS.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();
    }

    @Test
    @DisplayName("Delete user - NotFoundException")
    public void testDeleteUser_NotFoundException() throws Exception {

        Mockito.doThrow(new NotFoundException(HttpConstants.USER_NOT_FOUND))
                .when(userComponent).deleteUser(1L);

        ParameterizedTypeReference<Response<Void>> typeReference = new ParameterizedTypeReference<Response<Void>>() {
        };

        Response<Void> actualResponse = webTestClient.delete().uri("/v1/users/1")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.times(1)).deleteUser(1L);

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.USER_NOT_FOUND.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.USER_NOT_FOUND.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();
    }

    @Test
    @DisplayName("Delete user - Exception")
    public void testDeleteUser_Exception() throws Exception {

        Mockito.doThrow(new RuntimeException())
                .when(userComponent).deleteUser(1L);

        ParameterizedTypeReference<Response<Void>> typeReference = new ParameterizedTypeReference<Response<Void>>() {
        };

        Response<Void> actualResponse = webTestClient.delete().uri("/v1/users/1")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(typeReference)
                .returnResult()
                .getResponseBody();

        Mockito.verify(userComponent, Mockito.times(1)).deleteUser(1L);

        Assertions.assertThat(actualResponse.getStatus().getCode()).isEqualTo(HttpConstants.INTERNAL_SERVER_ERROR.getCode());
        Assertions.assertThat(actualResponse.getStatus().getMessage()).isEqualTo(HttpConstants.INTERNAL_SERVER_ERROR.getDesc());

        Assertions.assertThat(actualResponse.getData()).isNull();
    }

}