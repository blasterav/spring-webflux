package com.phoosop.reactive.converter;

import com.phoosop.reactive.exception.StatusConstants;
import com.phoosop.reactive.model.command.UserCommand;
import com.phoosop.reactive.model.enums.UserLevel;
import com.phoosop.reactive.model.enums.UserStatus;
import com.phoosop.reactive.model.enums.UserType;
import com.phoosop.reactive.model.request.CreateUserRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CreateUserRequestToUserCommandConverterTest {

    @InjectMocks
    private CreateUserRequestToUserCommandConverterImpl createUserRequestToUserCommandConverter;

    @Test
    public void testConvert() {
        CreateUserRequest source = new CreateUserRequest()
                .setCardId("cardId")
                .setFirstName("firstName")
                .setSecondName("secondName")
                .setType(UserType.USER.getValue())
                .setStatus(UserStatus.ACTIVE.getValue())
                .setDateOfBirth("27-11-1991")
                .setAge(18)
                .setMobileNumber("12345678901")
                .setMobileBrand("Apple");
        UserCommand result = createUserRequestToUserCommandConverter.convert(source);
        assertThat(result.getId()).isNull();
        assertThat(result.getCardId()).isEqualTo("cardId");
        assertThat(result.getFirstName()).isEqualTo("firstName");
        assertThat(result.getSecondName()).isEqualTo("secondName");
        assertThat(result.getDateOfBirth()).isEqualTo("27-11-1991");
        assertThat(result.getType()).isEqualTo(UserType.USER);
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(result.getLevel()).isNull();
        assertThat(result.getAge()).isEqualTo(18);
    }

}