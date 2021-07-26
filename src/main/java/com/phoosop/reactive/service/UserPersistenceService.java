package com.phoosop.reactive.service;

import com.phoosop.reactive.model.command.UserCommand;
import com.phoosop.reactive.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserPersistenceService {

    private final R2dbcEntityTemplate template;
    private final ConversionService conversionService;

    public Mono<UserCommand> save(UserCommand userCommand) {
        return template.insert(UserEntity.class)
                .using(conversionService.convert(userCommand, UserEntity.class))
                .map(item -> {
                    userCommand.setId(item.getId());
                    return userCommand;
                });
    }

    public Flux<UserCommand> findAll() {
        return template.select(UserEntity.class).all()
                .map(item -> conversionService.convert(item, UserCommand.class));
    }

}
