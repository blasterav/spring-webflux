package com.phoosop.reactive.converter;

import com.phoosop.reactive.model.command.UserCommand;
import com.phoosop.reactive.model.enums.UserStatus;
import com.phoosop.reactive.model.enums.UserType;
import com.phoosop.reactive.model.response.UserShortResponse;
import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;

@Mapper(componentModel = "spring")
public abstract class UserCommandToUserShortResponseConverter implements Converter<UserCommand, UserShortResponse> {

    public abstract UserShortResponse convert(UserCommand source);

    public int userStatusToInt(UserStatus userStatus) {
        return userStatus.getValue();
    }

    public String userTypeToString(UserType userType) {
        return userType.getValue();
    }

}
