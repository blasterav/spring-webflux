package com.phoosop.reactive.converter;

import com.phoosop.reactive.exception.ServiceException;
import com.phoosop.reactive.exception.StatusConstants;
import com.phoosop.reactive.model.command.UserCommand;
import com.phoosop.reactive.model.entity.UserEntity;
import com.phoosop.reactive.model.enums.UserLevel;
import com.phoosop.reactive.model.enums.UserStatus;
import com.phoosop.reactive.model.enums.UserType;
import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;

import static com.phoosop.reactive.exception.StatusConstants.*;

@Mapper(componentModel = "spring")
public abstract class UserEntityToUserCommandConverter implements Converter<UserEntity, UserCommand> {

    public abstract UserCommand convert(UserEntity userEntity);

    public UserStatus intToUserStatus(int id) {
        return UserStatus.find(id)
                .orElseThrow(() -> new ServiceException(HttpConstants.FAILED_TO_CONVERT_VALUE_TO_ENUM));
    }

    public UserType stringToUserType(String id) {
        return UserType.find(id)
                .orElseThrow(() -> new ServiceException(HttpConstants.FAILED_TO_CONVERT_VALUE_TO_ENUM));
    }

    public UserLevel intToUserLevel(int id) {
        return UserLevel.find(id)
                .orElseThrow(() -> new ServiceException(HttpConstants.FAILED_TO_CONVERT_VALUE_TO_ENUM));
    }
}
