package com.phoosop.reactive.converter;

import com.phoosop.reactive.exception.ServiceException;
import com.phoosop.reactive.exception.StatusConstants;
import com.phoosop.reactive.model.command.UserCommand;
import com.phoosop.reactive.model.enums.UserLevel;
import com.phoosop.reactive.model.enums.UserStatus;
import com.phoosop.reactive.model.enums.UserType;
import com.phoosop.reactive.model.request.CreateUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.core.convert.converter.Converter;

import static com.phoosop.reactive.exception.StatusConstants.*;


@Mapper(componentModel = "spring")
public abstract class CreateUserRequestToUserCommandConverter implements Converter<CreateUserRequest, UserCommand> {

    public abstract UserCommand convert(CreateUserRequest source);

    public UserStatus intToUserStatus(Integer id) {
        return UserStatus.find(id)
                .orElseThrow(() -> new ServiceException(HttpConstants.FAILED_TO_CONVERT_VALUE_TO_ENUM));
    }

    public UserType stringToUserType(String id) {
        return UserType.find(id)
                .orElseThrow(() -> new ServiceException(HttpConstants.FAILED_TO_CONVERT_VALUE_TO_ENUM));
    }

    public UserLevel intToUserLevel(Integer id) {
        return UserLevel.find(id)
                .orElseThrow(() -> new ServiceException(HttpConstants.FAILED_TO_CONVERT_VALUE_TO_ENUM));
    }

}
