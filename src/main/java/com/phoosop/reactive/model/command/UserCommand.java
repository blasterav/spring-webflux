package com.phoosop.reactive.model.command;

import com.phoosop.reactive.model.enums.UserLevel;
import com.phoosop.reactive.model.enums.UserStatus;
import com.phoosop.reactive.model.enums.UserType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class UserCommand {

    private Long id;

    private String cardId;

    private String firstName;

    private String secondName;

    private UserType type;

    private UserStatus status;

    private UserLevel level;

    private String dateOfBirth;

    private Integer age;

}
