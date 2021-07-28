package com.phoosop.reactive.service;

import com.phoosop.reactive.model.command.UserCommand;
import com.phoosop.reactive.model.entity.UserEntity;
import com.phoosop.reactive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Service
@RequiredArgsConstructor
public class UserPersistenceService {

    private final R2dbcEntityTemplate template;
    private final UserRepository userRepository;
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

    public Mono<UserCommand> findById(long id) {
        return template.select(UserEntity.class)
                .matching(query(where("id").is(id)))
                .one()
                .map(item -> conversionService.convert(item, UserCommand.class));
    }

    public Mono<Page<UserCommand>> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(page -> page.map(item -> conversionService.convert(item, UserCommand.class)));
    }

    public void delete(long id) {
        userRepository.deleteById(id);
    }

}
