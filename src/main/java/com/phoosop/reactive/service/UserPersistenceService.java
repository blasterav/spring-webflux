package com.phoosop.reactive.service;

import com.phoosop.reactive.model.CustomPage;
import com.phoosop.reactive.model.command.UserCommand;
import com.phoosop.reactive.model.entity.UserEntity;
import com.phoosop.reactive.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

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

    public Mono<UserCommand> findById(long id) {
        return template.select(UserEntity.class)
                .matching(query(where("id").is(id)))
                .one()
                .map(item -> conversionService.convert(item, UserCommand.class));
    }

    public Mono<CustomPage<UserCommand>> findAll(Pageable pageable) {
        Mono<Long> countMono = userRepository.count();
        long skip = pageable.getPageNumber() == 1 ? 0 : (long) pageable.getPageNumber() * pageable.getPageSize();
        Mono<List<UserCommand>> userCommandFlux = userRepository.findAll()
                .skip(skip)
                .take(pageable.getPageSize())
                .map(item -> conversionService.convert(item, UserCommand.class))
                .collectList();

        return Mono.zip(countMono, userCommandFlux, (aLong, list) -> new CustomPage<UserCommand>()
                .setContent(list)
                .setNumber(pageable.getPageNumber())
                .setSize(list.size())
                .setTotalElements(aLong));
    }

    public void delete(long id) {
        userRepository.deleteById(id);
    }

}
