package com.phoosop.reactive.converter;

import com.phoosop.reactive.model.command.UserCommand;
import com.phoosop.reactive.model.entity.UserEntity;
import com.phoosop.reactive.model.enums.UserLevel;
import com.phoosop.reactive.model.enums.UserStatus;
import com.phoosop.reactive.model.enums.UserType;
import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;

@Mapper(componentModel = "spring")
public abstract class UserCommandToUserEntityConverter implements Converter<UserCommand, UserEntity> {

    public abstract UserEntity convert(UserCommand source);

    public int userStatusToInt(UserStatus userStatus) {
        return userStatus.getValue();
    }

    public String userTypeToString(UserType userType) {
        return userType.getValue();
    }

    public int userLevelToInt(UserLevel userLevel) {
        return userLevel.getValue();
    }

}
