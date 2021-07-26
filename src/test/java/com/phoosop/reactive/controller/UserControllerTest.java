package com.phoosop.reactive.controller;

import com.phoosop.reactive.component.UserComponent;
import com.phoosop.reactive.exception.ServiceException;
import com.phoosop.reactive.model.Response;
import com.phoosop.reactive.model.command.UserCommand;
import com.phoosop.reactive.model.enums.UserLevel;
import com.phoosop.reactive.model.enums.UserStatus;
import com.phoosop.reactive.model.enums.UserType;
import com.phoosop.reactive.model.request.CreateUserRequest;
import com.phoosop.reactive.model.response.UserResponse;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

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

}