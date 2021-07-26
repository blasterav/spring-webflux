package com.phoosop.reactive.component;

import com.phoosop.reactive.model.command.UserCommand;
import com.phoosop.reactive.model.enums.UserLevel;
import com.phoosop.reactive.model.request.CreateUserRequest;
import com.phoosop.reactive.service.UserPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class UserComponent {

    private final UserPersistenceService userPersistenceService;
    private final ConversionService conversionService;

    public Mono<UserCommand> createUser(CreateUserRequest request) {
        UserCommand userCommand = conversionService.convert(request, UserCommand.class);
        userCommand.setLevel(UserLevel.LEVEL_1);
        return userPersistenceService.save(userCommand);
    }

}
